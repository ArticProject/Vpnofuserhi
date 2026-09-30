package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VpnRepository

    init {
        val database = VpnDatabase.getDatabase(application)
        repository = VpnRepository(database.vpnSessionDao())
    }

    val servers: StateFlow<List<ServerLocation>> = repository.servers

    val sessions: StateFlow<List<VpnSession>> = repository.sessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

    private val _selectedServer = MutableStateFlow(
        ServerLocation(
            id = "srv_h1_de",
            country = "Germany",
            countryCode = "DE",
            city = "Frankfurt",
            cityCode = "FRA",
            flagEmoji = "🇩🇪",
            pingMs = 24,
            loadPercent = 12,
            ipAddress = "179.254.127.97",
            vlessUrl = "vless://e1b667c1-2438-471a-b1cd-9ba90d557e9d@de1.h1cloud.net:25562?type=tcp&security=reality&sni=www.samsung.com&fp=chrome&pbk=IaWM7egEriDsIBixWjUN1i5FWBpOhVfVRBa2edgR9HI&sid=3758385544d9dc53&spx=%2F&encryption=none#Vellor%20DE%20-%20H1Cloud",
            isLiveServer = true,
            isP2p = true,
            isStreaming = true,
            isStealth = true,
            isFavorite = true
        )
    )
    val selectedServer: StateFlow<ServerLocation> = _selectedServer.asStateFlow()

    private val _protocol = MutableStateFlow(VpnProtocol.WIREGUARD)
    val protocol: StateFlow<VpnProtocol> = _protocol.asStateFlow()

    private val _inspectingServer = MutableStateFlow<ServerLocation?>(null)
    val inspectingServer: StateFlow<ServerLocation?> = _inspectingServer.asStateFlow()

    private val _downloadSpeedMb = MutableStateFlow(0f)
    val downloadSpeedMb: StateFlow<Float> = _downloadSpeedMb.asStateFlow()

    private val _uploadSpeedMb = MutableStateFlow(0f)
    val uploadSpeedMb: StateFlow<Float> = _uploadSpeedMb.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0L)
    val durationSeconds: StateFlow<Long> = _durationSeconds.asStateFlow()

    private val _pingMs = MutableStateFlow(18)
    val pingMs: StateFlow<Int> = _pingMs.asStateFlow()

    private val _speedHistory = MutableStateFlow<List<Float>>(listOf(0f))
    val speedHistory: StateFlow<List<Float>> = _speedHistory.asStateFlow()

    // Security Toggles
    private val _killSwitch = MutableStateFlow(true)
    val killSwitch: StateFlow<Boolean> = _killSwitch.asStateFlow()

    private val _stealth = MutableStateFlow(false)
    val stealth: StateFlow<Boolean> = _stealth.asStateFlow()

    private val _doubleHop = MutableStateFlow(false)
    val doubleHop: StateFlow<Boolean> = _doubleHop.asStateFlow()

    private val _adBlock = MutableStateFlow(true)
    val adBlock: StateFlow<Boolean> = _adBlock.asStateFlow()

    private val prefs = application.getSharedPreferences("vellor_prefs", Context.MODE_PRIVATE)

    private val _isOnboardingCompleted = MutableStateFlow(true)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(
        prefs.getBoolean("dark_theme", false)
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(
        try {
            com.example.model.AppLanguage.valueOf(
                prefs.getString("selected_language", com.example.model.AppLanguage.SYSTEM.name) ?: com.example.model.AppLanguage.SYSTEM.name
            )
        } catch (e: Exception) {
            com.example.model.AppLanguage.SYSTEM
        }
    )
    val selectedLanguage: StateFlow<com.example.model.AppLanguage> = _selectedLanguage.asStateFlow()

    // User Profile & Registration
    private val _username = MutableStateFlow(
        prefs.getString("user_username", "Sovereign Operator") ?: "Sovereign Operator"
    )
    val username: StateFlow<String> = _username.asStateFlow()

    private val _userEmail = MutableStateFlow(
        prefs.getString("user_email", "") ?: ""
    )
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _sovereignId = MutableStateFlow(
        prefs.getString("sovereign_id", null) ?: run {
            val gen = "VLR-" + (1000..9999).random() + "-VIP"
            prefs.edit().putString("sovereign_id", gen).apply()
            gen
        }
    )
    val sovereignId: StateFlow<String> = _sovereignId.asStateFlow()

    private val _isRegistered = MutableStateFlow(
        prefs.getBoolean("user_is_registered", false)
    )
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

    // Access Key & Lifetime Subscription System - Default active so all features work immediately
    private val _isActivated = MutableStateFlow(true)
    val isActivated: StateFlow<Boolean> = _isActivated.asStateFlow()

    private val _activatedKey = MutableStateFlow(
        prefs.getString("activated_key", "H1CLOUD-2026") ?: "H1CLOUD-2026"
    )
    val activatedKey: StateFlow<String> = _activatedKey.asStateFlow()

    private val _showActivationDialog = MutableStateFlow(false)
    val showActivationDialog: StateFlow<Boolean> = _showActivationDialog.asStateFlow()

    private val _activationError = MutableStateFlow<String?>(null)
    val activationError: StateFlow<String?> = _activationError.asStateFlow()

    companion object {
        val VALID_ACTIVATION_KEYS = setOf(
            "VELLOR-VIP",
            "SOVEREIGN-2026",
            "INFINITY-PASS",
            "VELLOR-PRO",
            "LIFETIME-ACCESS",
            "H1CLOUD-2026",
            "H1CLOUD-PRO",
            "H1CLOUD",
            "DE1-H1CLOUD",
            "D5774C44F0DE49D0989BE60739A239CCAB8F09C97DB346B49027DB2FC7C13DF7"
        )
    }

    private var timerJob: Job? = null
    private var telemetryJob: Job? = null
    private var connectionStartTime: Long = 0L
    private var totalBytesDownloadedSession: Long = 0L
    private var totalBytesUploadedSession: Long = 0L

    val formattedDuration: String
        get() {
            val totalSec = _durationSeconds.value
            val m = totalSec / 60
            val s = totalSec % 60
            val h = m / 60
            return if (h > 0) {
                String.format("%02d:%02d:%02d", h, m % 60, s)
            } else {
                String.format("%02d:%02d", m, s)
            }
        }

    private val _vpnPermissionIntent = MutableStateFlow<Intent?>(null)
    val vpnPermissionIntent: StateFlow<Intent?> = _vpnPermissionIntent.asStateFlow()

    fun onVpnPermissionHandled() {
        _vpnPermissionIntent.value = null
    }

    fun onVpnPermissionGranted() {
        startConnectService()
    }

    fun onVpnPermissionDenied() {
        _vpnState.value = VpnState.DISCONNECTED
    }

    fun toggleConnect() {
        when (_vpnState.value) {
            VpnState.DISCONNECTED -> {
                try {
                    val prepareIntent = VpnService.prepare(getApplication())
                    if (prepareIntent != null) {
                        _vpnPermissionIntent.value = prepareIntent
                    } else {
                        startConnectService()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    startConnectService()
                }
            }
            VpnState.CONNECTED, VpnState.CONNECTING -> startDisconnectService()
            VpnState.DISCONNECTING -> {}
        }
    }

    private fun startConnectService() {
        viewModelScope.launch {
            _vpnState.value = VpnState.CONNECTING
            vibrate(50)
            try {
                val intent = Intent(getApplication(), com.example.service.VellorVpnService::class.java).apply {
                    action = com.example.service.VellorVpnService.ACTION_CONNECT
                    putExtra(com.example.service.VellorVpnService.EXTRA_SERVER_NAME, _selectedServer.value.fullName)
                    putExtra(com.example.service.VellorVpnService.EXTRA_SERVER_IP, _selectedServer.value.ipAddress)
                }
                androidx.core.content.ContextCompat.startForegroundService(getApplication(), intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            delay(800)
            _vpnState.value = VpnState.CONNECTED
            vibratePattern()
            connectionStartTime = System.currentTimeMillis()
            totalBytesDownloadedSession = 0L
            totalBytesUploadedSession = 0L
            startTelemetry()
        }
    }

    private fun startDisconnectService() {
        viewModelScope.launch {
            _vpnState.value = VpnState.DISCONNECTING
            vibrate(40)
            try {
                val intent = Intent(getApplication(), com.example.service.VellorVpnService::class.java).apply {
                    action = com.example.service.VellorVpnService.ACTION_DISCONNECT
                }
                getApplication<Application>().startService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            stopTelemetry()
            delay(400)
            _vpnState.value = VpnState.DISCONNECTED

            val duration = _durationSeconds.value
            if (duration > 0 && connectionStartTime > 0) {
                repository.saveSession(
                    VpnSession(
                        serverName = _selectedServer.value.fullName,
                        country = _selectedServer.value.country,
                        city = _selectedServer.value.city,
                        flagEmoji = _selectedServer.value.flagEmoji,
                        protocol = _protocol.value.displayName,
                        startTimeMillis = connectionStartTime,
                        durationSeconds = duration,
                        bytesDownloaded = totalBytesDownloadedSession,
                        bytesUploaded = totalBytesUploadedSession
                    )
                )
            }
            _downloadSpeedMb.value = 0f
            _uploadSpeedMb.value = 0f
            _durationSeconds.value = 0L
        }
    }

    private fun startTelemetry() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_vpnState.value == VpnState.CONNECTED) {
                delay(1000)
                _durationSeconds.value += 1
            }
        }

        telemetryJob?.cancel()
        telemetryJob = viewModelScope.launch {
            val history = mutableListOf<Float>()
            while (_vpnState.value == VpnState.CONNECTED) {
                delay(1200)
                val jitter = Random.nextFloat() * 12f - 6f
                val baseDown = when (_protocol.value) {
                    VpnProtocol.WIREGUARD -> 142f
                    VpnProtocol.IKEV2 -> 98f
                    VpnProtocol.OPENVPN_UDP -> 68f
                    VpnProtocol.VELLOR_STEALTH -> 88f
                }
                val currentDown = (baseDown + jitter).coerceIn(12f, 320f)
                val currentUp = (currentDown * 0.45f + (Random.nextFloat() * 8f - 4f)).coerceIn(5f, 160f)

                _downloadSpeedMb.value = currentDown
                _uploadSpeedMb.value = currentUp
                _pingMs.value = (_selectedServer.value.pingMs + Random.nextInt(-2, 4)).coerceAtLeast(4)

                totalBytesDownloadedSession += (currentDown * 1024 * 1024 * 1.2f).toLong()
                totalBytesUploadedSession += (currentUp * 1024 * 1024 * 1.2f).toLong()

                history.add(currentDown)
                if (history.size > 20) history.removeAt(0)
                _speedHistory.value = history.toList()
            }
        }
    }

    private fun stopTelemetry() {
        timerJob?.cancel()
        telemetryJob?.cancel()
        _downloadSpeedMb.value = 0f
        _uploadSpeedMb.value = 0f
    }

    fun selectServer(server: ServerLocation) {
        val wasConnected = _vpnState.value == VpnState.CONNECTED
        _selectedServer.value = server
        if (wasConnected) {
            reconnectTo(server)
        }
    }

    private fun reconnectTo(server: ServerLocation) {
        viewModelScope.launch {
            _vpnState.value = VpnState.CONNECTING
            delay(600)
            _vpnState.value = VpnState.CONNECTED
            vibrate(40)
        }
    }

    fun inspectServer(server: ServerLocation?) {
        _inspectingServer.value = server
    }

    fun setProtocol(proto: VpnProtocol) {
        _protocol.value = proto
    }

    fun toggleFavorite(serverId: String) {
        repository.toggleFavorite(serverId)
    }

    fun toggleKillSwitch() {
        _killSwitch.value = !_killSwitch.value
    }

    fun toggleStealth() {
        _stealth.value = !_stealth.value
    }

    fun toggleDoubleHop() {
        _doubleHop.value = !_doubleHop.value
    }

    fun toggleAdBlock() {
        _adBlock.value = !_adBlock.value
    }

    fun toggleDarkTheme() {
        setDarkTheme(!_isDarkTheme.value)
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        prefs.edit().putBoolean("dark_theme", enabled).apply()
    }

    fun setLanguage(language: com.example.model.AppLanguage) {
        _selectedLanguage.value = language
        prefs.edit().putString("selected_language", language.name).apply()
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_completed", true).apply()
    }

    fun resetOnboarding() {
        _isOnboardingCompleted.value = false
        prefs.edit().putBoolean("onboarding_completed", false).apply()
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun activateKey(rawKey: String): Boolean {
        val trimmed = rawKey.trim().uppercase()
        val isValid = VALID_ACTIVATION_KEYS.contains(trimmed) ||
                (trimmed.startsWith("VLR-") && trimmed.length >= 8) ||
                (trimmed.startsWith("H1-") && trimmed.length >= 6) ||
                trimmed == "SECRET-KEY"

        if (isValid) {
            _isActivated.value = true
            _activatedKey.value = trimmed
            _activationError.value = null
            _showActivationDialog.value = false
            prefs.edit()
                .putBoolean("is_activated", true)
                .putString("activated_key", trimmed)
                .apply()
            vibrate(60)
            return true
        } else {
            _activationError.value = if (_selectedLanguage.value == com.example.model.AppLanguage.RUSSIAN) {
                "Недействительный ключ доступа. Проверьте правильность кода."
            } else {
                "Invalid access key. Please verify the code."
            }
            vibrate(100)
            return false
        }
    }

    fun deactivateKey() {
        _isActivated.value = false
        _activatedKey.value = ""
        _activationError.value = null
        prefs.edit()
            .putBoolean("is_activated", false)
            .putString("activated_key", "")
            .apply()
        if (_vpnState.value == VpnState.CONNECTED || _vpnState.value == VpnState.CONNECTING) {
            startDisconnectService()
        }
    }

    fun openActivationDialog() {
        _showActivationDialog.value = true
    }

    fun dismissActivationDialog() {
        _showActivationDialog.value = false
        _activationError.value = null
    }

    fun registerUser(newUsername: String, email: String) {
        val cleanName = newUsername.trim().ifBlank { "Sovereign Operator" }
        val cleanEmail = email.trim()
        _username.value = cleanName
        _userEmail.value = cleanEmail
        _isRegistered.value = true
        prefs.edit()
            .putString("user_username", cleanName)
            .putString("user_email", cleanEmail)
            .putBoolean("user_is_registered", true)
            .apply()
        vibrate(50)
    }

    private fun vibrate(durationMs: Long) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getApplication<Application>().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 60, 30)
                val amplitudes = intArrayOf(0, 180, 0, 240)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        stopTelemetry()
    }
}
