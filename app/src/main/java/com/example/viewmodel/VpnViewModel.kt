package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VpnDatabase
import com.example.data.VpnRepository
import com.example.model.ServerLocation
import com.example.model.VpnProtocol
import com.example.model.VpnSession
import com.example.model.VpnState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VpnRepository

    init {
        val database = VpnDatabase.getDatabase(application)
        repository = VpnRepository(database.vpnSessionDao())
    }

    val servers: StateFlow<List<ServerLocation>> = repository.servers
    val sessions: StateFlow<List<VpnSession>> = repository.allSessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState = _vpnState.asStateFlow()

    private val _selectedServer = MutableStateFlow(
        ServerLocation(
            id = "ch_zrh_01",
            country = "Switzerland",
            city = "Zurich",
            countryCode = "CH",
            flagEmoji = "🇨🇭",
            pingMs = 12,
            loadPercent = 28,
            ipAddress = "185.220.101.42",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = true,
            isStealth = true,
            isFavorite = true
        )
    )
    val selectedServer = _selectedServer.asStateFlow()

    private val _protocol = MutableStateFlow(VpnProtocol.WIREGUARD)
    val protocol = _protocol.asStateFlow()

    private val _downloadSpeedMb = MutableStateFlow(0f)
    val downloadSpeedMb = _downloadSpeedMb.asStateFlow()

    private val _uploadSpeedMb = MutableStateFlow(0f)
    val uploadSpeedMb = _uploadSpeedMb.asStateFlow()

    private val _speedHistory = MutableStateFlow(List(14) { 0f })
    val speedHistory = _speedHistory.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0L)
    val durationSeconds = _durationSeconds.asStateFlow()

    private val _pingMs = MutableStateFlow(12)
    val pingMs = _pingMs.asStateFlow()

    private val _killSwitch = MutableStateFlow(true)
    val killSwitch = _killSwitch.asStateFlow()

    private val _stealth = MutableStateFlow(false)
    val stealth = _stealth.asStateFlow()

    private val _doubleHop = MutableStateFlow(false)
    val doubleHop = _doubleHop.asStateFlow()

    private val _adBlock = MutableStateFlow(true)
    val adBlock = _adBlock.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    private val _inspectingServer = MutableStateFlow<ServerLocation?>(null)
    val inspectingServer = _inspectingServer.asStateFlow()

    private var connectionStartTime: Long = 0L
    private var telemetryJob: Job? = null
    private var timerJob: Job? = null

    private var totalBytesDownloadedSession: Long = 0L
    private var totalBytesUploadedSession: Long = 0L

    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun selectServer(server: ServerLocation) {
        _selectedServer.value = server
        _pingMs.value = server.pingMs
        if (_vpnState.value == VpnState.CONNECTED) {
            // Reconnect to new node
            reconnectTo(server)
        }
    }

    fun setProtocol(proto: VpnProtocol) {
        _protocol.value = proto
    }

    fun toggleFavorite(serverId: String) {
        repository.toggleFavorite(serverId)
    }

    fun inspectServer(server: ServerLocation?) {
        _inspectingServer.value = server
    }

    fun toggleKillSwitch(enabled: Boolean) { _killSwitch.value = enabled }
    fun toggleStealth(enabled: Boolean) { _stealth.value = enabled }
    fun toggleDoubleHop(enabled: Boolean) { _doubleHop.value = enabled }
    fun toggleAdBlock(enabled: Boolean) { _adBlock.value = enabled }

    fun toggleConnect() {
        when (_vpnState.value) {
            VpnState.DISCONNECTED -> startConnect()
            VpnState.CONNECTED -> startDisconnect()
            VpnState.CONNECTING, VpnState.DISCONNECTING -> { /* no-op during transition */ }
        }
    }

    fun autoConnectFastest() {
        val fastest = servers.value.minByOrNull { it.pingMs } ?: return
        selectServer(fastest)
        if (_vpnState.value != VpnState.CONNECTED) {
            startConnect()
        }
    }

    private fun startConnect() {
        vibrate(40)
        _vpnState.value = VpnState.CONNECTING

        viewModelScope.launch {
            delay(1100) // Realistic cryptographic key exchange & handshake
            _vpnState.value = VpnState.CONNECTED
            vibratePattern(longArrayOf(0, 35, 60, 45))
            connectionStartTime = System.currentTimeMillis()
            _durationSeconds.value = 0L
            totalBytesDownloadedSession = 0L
            totalBytesUploadedSession = 0L

            startTelemetry()
        }
    }

    private fun startDisconnect() {
        vibrate(30)
        _vpnState.value = VpnState.DISCONNECTING
        stopTelemetry()

        viewModelScope.launch {
            delay(600)
            _vpnState.value = VpnState.DISCONNECTED
            vibrate(50)

            // Save completed session to Room DB
            val sessionDuration = _durationSeconds.value
            if (sessionDuration > 1) {
                val current = _selectedServer.value
                val session = VpnSession(
                    serverName = current.fullName,
                    city = current.city,
                    country = current.country,
                    flagEmoji = current.flagEmoji,
                    startTimeMillis = connectionStartTime,
                    durationSeconds = sessionDuration,
                    bytesDownloaded = totalBytesDownloadedSession.coerceAtLeast(1024 * 1024 * 4),
                    bytesUploaded = totalBytesUploadedSession.coerceAtLeast(1024 * 512),
                    protocol = _protocol.value.displayName
                )
                repository.saveSession(session)
            }

            _downloadSpeedMb.value = 0f
            _uploadSpeedMb.value = 0f
            _durationSeconds.value = 0L
            _speedHistory.value = List(14) { 0f }
        }
    }

    private fun reconnectTo(server: ServerLocation) {
        viewModelScope.launch {
            _vpnState.value = VpnState.CONNECTING
            stopTelemetry()
            delay(900)
            _selectedServer.value = server
            _vpnState.value = VpnState.CONNECTED
            vibrate(40)
            startTelemetry()
        }
    }

    private fun startTelemetry() {
        stopTelemetry()

        // Duration timer
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _durationSeconds.value += 1
            }
        }

        // Live speed & ping generator
        telemetryJob = viewModelScope.launch {
            val random = Random(System.currentTimeMillis())
            var baseDown = 34.0f + random.nextFloat() * 18.0f
            var baseUp = 8.5f + random.nextFloat() * 6.0f

            while (isActive) {
                val jitter = (random.nextFloat() - 0.48f) * 6.0f
                val down = (baseDown + jitter).coerceIn(18.0f, 95.0f)
                val up = (baseUp + jitter * 0.3f).coerceIn(3.0f, 28.0f)

                _downloadSpeedMb.value = down
                _uploadSpeedMb.value = up

                // Accumulate bytes
                totalBytesDownloadedSession += (down * 1024 * 1024 / 2).toLong()
                totalBytesUploadedSession += (up * 1024 * 1024 / 2).toLong()

                // Update sparkline
                val currentHist = _speedHistory.value.toMutableList()
                if (currentHist.size >= 14) currentHist.removeAt(0)
                currentHist.add(down)
                _speedHistory.value = currentHist

                // Jittered ping
                val basePing = _selectedServer.value.pingMs
                val pingJitter = random.nextInt(-2, 3)
                _pingMs.value = (basePing + pingJitter).coerceAtLeast(6)

                delay(800)
            }
        }
    }

    private fun stopTelemetry() {
        timerJob?.cancel()
        timerJob = null
        telemetryJob?.cancel()
        telemetryJob = null
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    val formattedDuration: String
        get() {
            val secs = _durationSeconds.value
            val hours = secs / 3600
            val minutes = (secs % 3600) / 60
            val seconds = secs % 60
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }

    private fun vibrate(durationMillis: Long) {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMillis)
                }
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        stopTelemetry()
    }
}
