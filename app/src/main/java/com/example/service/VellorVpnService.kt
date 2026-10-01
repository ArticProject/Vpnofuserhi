package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.model.VpnState
import kotlinx.coroutines.flow.MutableStateFlow

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.example.vpn.VlessConfig
import com.example.vpn.VpnEngine
import com.example.vpn.XrayEngine
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

data class VpnTelemetry(
    val downloaded: Long = 0, val uploaded: Long = 0,
    val downloadMbps: Float = 0f, val uploadMbps: Float = 0f,
    val pingMs: Int = 0, val startedAt: Long = 0
)

class VellorVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null
    private var engine: VpnEngine? = null
    private var trafficTask: ScheduledFuture<*>? = null
    private val generation = AtomicLong()
    private val main = Handler(Looper.getMainLooper())
    private var currentServerName: String = "Vellor"
    private var currentServerCountry: String = ""
    private var currentPingMs: Int = 0

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        const val EXTRA_SERVER_NAME = "extra_server_name"
        const val EXTRA_VLESS_URL = "extra_vless_url"
        const val EXTRA_SERVER_COUNTRY = "extra_server_country"
        const val CHANNEL_ID = "vellor_vpn_channel"
        const val NOTIFICATION_ID = 1001
        private val worker = Executors.newSingleThreadScheduledExecutor { task ->
            Thread(task, "vellor-vpn").apply { isDaemon = true }
        }
        internal var engineFactory: (Context) -> VpnEngine = { XrayEngine(it) }
        internal val connectionState = MutableStateFlow(VpnState.DISCONNECTED)
        internal val connectionFailed = MutableStateFlow(false)
        internal val errorMessage = MutableStateFlow("")
        internal val telemetry = MutableStateFlow(VpnTelemetry())
        @Volatile private var owner: VellorVpnService? = null
        @Volatile var isRunning = false
            private set
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_CONNECT) {
            val serverName = intent.getStringExtra(EXTRA_SERVER_NAME)
            val vlessUrl = intent.getStringExtra(EXTRA_VLESS_URL)
            val serverCountry = intent.getStringExtra(EXTRA_SERVER_COUNTRY) ?: ""
            if (vlessUrl.isNullOrBlank()) {
                val storage = com.example.subscription.SubscriptionStorage(this)
                val server = com.example.subscription.H1Access.DEFAULT_SERVERS.firstOrNull { it.id == storage.selectedServerId }
                    ?: com.example.subscription.H1Access.FINLAND_SERVER
                startVpn(server.fullName, server.country, server.vlessUrl)
            } else {
                startVpn(serverName ?: "Vellor", serverCountry, vlessUrl)
            }
        } else stopVpn()
        return START_NOT_STICKY
    }

    private fun startVpn(serverName: String, serverCountry: String, profile: String) {
        val request = generation.incrementAndGet()
        owner = this
        currentServerName = serverName
        currentServerCountry = serverCountry
        currentPingMs = 0
        connectionFailed.value = false
        errorMessage.value = ""
        connectionState.value = VpnState.CONNECTING
        try {
            check(prepare(this) == null)
            createNotificationChannel()
            ServiceCompat.startForeground(this, NOTIFICATION_ID,
                createNotification("Подключение к $serverCountry...", 0),
                if (Build.VERSION.SDK_INT >= 34) ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED else 0)
        } catch (_: Exception) {
            fail(request, "Не удалось запустить VPN. Проверьте разрешение Android.")
            return
        }
        worker.execute {
            releaseTunnel()
            if (!isCurrent(request)) return@execute
            try {
                val config = VlessConfig.build(profile)
                check(prepare(this) == null)
                val builder = Builder().setSession("Vellor ($serverName)")
                    .addAddress("10.8.0.2", 30)
                    .addAddress("fd66:7665:6c6c::2", 126)
                    .addRoute("0.0.0.0", 0).addRoute("::", 0)
                    .addDnsServer("1.1.1.1").addDnsServer("8.8.8.8")
                    .addDisallowedApplication(packageName).setMtu(1500)
                if (Build.VERSION.SDK_INT >= 29) builder.setMetered(false)
                vpnInterface = builder.establish() ?: error("VPN permission revoked")
                if (!isCurrent(request)) { releaseTunnel(); return@execute }
                val core = engineFactory(this).also { engine = it }
                core.start(config, vpnInterface!!.fd)
                val ping = core.probe()
                if (!isCurrent(request)) { releaseTunnel(); return@execute }
                core.readTraffic()
                currentPingMs = ping.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                telemetry.value = VpnTelemetry(pingMs = currentPingMs,
                    startedAt = System.currentTimeMillis())
                isRunning = true
                connectionState.value = VpnState.CONNECTED
                main.post {
                    if (isCurrent(request)) {
                        getSystemService(NotificationManager::class.java)
                            .notify(NOTIFICATION_ID, createNotification("$serverCountry • ${currentPingMs}ms", currentPingMs))
                    }
                }
                var lastSample = SystemClock.elapsedRealtime()
                var ticks = 0
                trafficTask = worker.scheduleWithFixedDelay({
                    if (isCurrent(request)) {
                        try {
                            val bytes = core.readTraffic()
                            val now = SystemClock.elapsedRealtime()
                            val elapsed = (now - lastSample).coerceAtLeast(1)
                            lastSample = now
                            ticks++
                            
                            // Обновляем пинг каждые 3 секунды
                            if (ticks % 3 == 0) {
                                try {
                                    val measured = core.probe().coerceAtMost(9999).toInt()
                                    if (measured in 1..2500) {
                                        currentPingMs = measured
                                        // Обновляем уведомление с новым пингом
                                        main.post {
                                            if (isCurrent(request)) {
                                                getSystemService(NotificationManager::class.java)
                                                    .notify(NOTIFICATION_ID, createNotification("$currentServerCountry • ${currentPingMs}ms", currentPingMs))
                                            }
                                        }
                                    }
                                } catch (_: Exception) {
                                    // Оставляем текущий пинг
                                }
                            }
                            
                            val previous = telemetry.value
                            telemetry.value = previous.copy(
                                downloaded = previous.downloaded + bytes.downloaded,
                                uploaded = previous.uploaded + bytes.uploaded,
                                downloadMbps = bytes.downloaded * 8f / (elapsed * 1000f),
                                uploadMbps = bytes.uploaded * 8f / (elapsed * 1000f),
                                pingMs = currentPingMs
                            )
                        } catch (_: Exception) {
                            fail(request, "VPN остановлен из-за ошибки ядра. Подключитесь повторно.")
                        }
                    }
                }, 1, 1, TimeUnit.SECONDS)
            } catch (_: Exception) {
                fail(request, "Не удалось подключиться. Проверьте интернет и VLESS-ключ сервера.")
            } catch (_: LinkageError) {
                fail(request, "Не удалось загрузить VPN-ядро для этого устройства.")
            }
        }
    }

    private fun isCurrent(request: Long) = owner === this && generation.get() == request

    private fun fail(request: Long, message: String) {
        main.post {
            if (isCurrent(request)) {
                errorMessage.value = message
                connectionFailed.value = true
                stopVpn()
            }
        }
    }

    private fun releaseTunnel() {
        trafficTask?.cancel(false)
        trafficTask = null
        try { engine?.close() } catch (_: Exception) { }
        engine = null
        try { vpnInterface?.close() } catch (_: Exception) { }
        vpnInterface = null
        if (owner === this) isRunning = false
    }

    private fun stopVpn() {
        val request = generation.incrementAndGet()
        if (owner === this) connectionState.value = VpnState.DISCONNECTING
        worker.execute {
            releaseTunnel()
            main.post {
                if (generation.get() == request) {
                    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                    if (owner === this) {
                        telemetry.value = VpnTelemetry()
                        connectionState.value = VpnState.DISCONNECTED
                    }
                    stopSelf()
                }
            }
        }
    }

    override fun onRevoke() = stopVpn()

    override fun onDestroy() {
        generation.incrementAndGet()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        worker.execute {
            releaseTunnel()
            if (owner === this) {
                telemetry.value = VpnTelemetry()
                connectionState.value = VpnState.DISCONNECTED
                owner = null
            }
        }
        super.onDestroy()
    }

    private fun createNotification(statusText: String, pingMs: Int): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vellor VPN")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_vpn_stat)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(NotificationCompat.BigTextStyle().bigText(statusText))
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Vellor VPN Status", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
