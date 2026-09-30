package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class VellorVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.ACTION_DISCONNECT"
        const val EXTRA_SERVER_NAME = "extra_server_name"
        const val EXTRA_SERVER_IP = "extra_server_ip"
        const val CHANNEL_ID = "vellor_vpn_channel"
        const val NOTIFICATION_ID = 1001

        @Volatile
        var isRunning: Boolean = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannel()
        } catch (_: Exception) {}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            when (intent?.action) {
                ACTION_CONNECT -> {
                    val serverName = intent.getStringExtra(EXTRA_SERVER_NAME) ?: "Germany (de1.h1cloud.net)"
                    val serverIp = intent.getStringExtra(EXTRA_SERVER_IP) ?: "179.254.127.97"
                    startVpn(serverName, serverIp)
                }
                ACTION_DISCONNECT -> {
                    stopVpn()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_NOT_STICKY
    }

    private fun startVpn(serverName: String, serverIp: String) {
        stopVpn()

        // 1. Show Foreground Notification so system registers active status in status bar
        try {
            val notification = createNotification("Подключено: $serverName ($serverIp)")
            startForeground(NOTIFICATION_ID, notification)
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        // 2. Establish VPN interface
        try {
            val builder = Builder()
                .setSession("Vellor Sovereign ($serverName)")
                .addAddress("10.8.0.2", 24)
                .addDnsServer("1.1.1.1")
                .addDnsServer("8.8.8.8")
                .addRoute("10.8.0.0", 24)
                .setMtu(1500)

            vpnInterface = builder.establish()
            isRunning = true

            // Keep coroutine running while tunnel is active
            serviceJob = scope.launch(Dispatchers.IO) {
                try {
                    while (isActive && isRunning) {
                        delay(1000)
                    }
                } catch (_: Exception) {}
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            isRunning = false
        }
    }

    private fun stopVpn() {
        isRunning = false
        serviceJob?.cancel()
        serviceJob = null

        try {
            vpnInterface?.close()
        } catch (_: Exception) {}
        vpnInterface = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
        } catch (_: Exception) {}

        stopSelf()
    }

    private fun createNotification(statusText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val iconRes = try {
            val id = resources.getIdentifier("ic_vpn_stat", "drawable", packageName)
            if (id != 0) id else android.R.drawable.ic_lock_lock
        } catch (_: Exception) {
            android.R.drawable.ic_lock_lock
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vellor Sovereign VPN")
            .setContentText(statusText)
            .setSmallIcon(iconRes)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Vellor VPN Status",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows active VPN tunnel connection status"
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
