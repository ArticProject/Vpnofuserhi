package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VpnDatabase
import com.example.data.VpnRepository
import com.example.model.AppLanguage
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

    private val _protocol = MutableStateFlow(VpnProtocol.VLESS_REALITY)
    val protocol: StateFlow<VpnProtocol> = _protocol.asStateFlow()

    private val _inspectingServer = MutableStateFlow<ServerLocation?>(null)
    val inspectingServer: StateFlow<ServerLocation?> = _inspectingServer.asStateFlow()

    private val _downloadSpeedMb = MutableStateFlow(0f)
    val downloadSpeedMb: StateFlow<Float> = _downloadSpeedMb.asStateFlow()

    private val _uploadSpeedMb = MutableStateFlow(0f)
    val uploadSpeedMb: StateFlow<Float> = _uploadSpeedMb.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0L)
    val durationSeconds: StateFlow<Long> = _durationSeconds.asStateFlow()

    private val _pingMs = MutableStateFlow(24)
    val pingMs: StateFlow<Int> = _pingMs.asStateFlow()

    // Security Toggles
    private val _killSwitch = MutableStateFlow(true)
    val killSwitch: StateFlow<Boolean> = _killSwitch.asStateFlow()

    private val _stealth = MutableStateFlow(true)
    val stealth: StateFlow<Boolean> = _stealth.asStateFlow()

    private val _doubleHop = MutableStateFlow(false)
    val doubleHop: StateFlow<Boolean> = _doubleHop.asStateFlow()

    private val _adBlock = MutableStateFlow(true)
    val adBlock: StateFlow<Boolean> = _adBlock.asStateFlow()

    private val prefs = application.getSharedPreferences("vellor_prefs", Context.MODE_PRIVATE)

    private val _isOnboardingCompleted = MutableStateFlow(true)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(
        prefs.getBoolean("dark_theme", true)
    )
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(
        try {
            AppLanguage.valueOf(
                prefs.getString("selected_language", AppLanguage.RUSSIAN.name) ?: AppLanguage.RUSSIAN.name
            )
        } catch (_: Exception) {
            AppLanguage.RUSSIAN
        }
    )
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _username = MutableStateFlow(
        prefs.getString("user_username", "Sovereign Operator") ?: "Sovereign Operator"
    )
    val username: StateFlow<String> = _username.asStateFlow()

    private val _userEmail = MutableStateFlow(
        prefs.getString("user_email", "operator@vellor.network") ?: "operator@vellor.network"
    )
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _sovereignId = MutableStateFlow(
        prefs.getString("sovereign_id", "VLR-8821-VIP") ?: "VLR-8821-VIP"
    )
    val sovereignId: StateFlow<String> = _sovereignId.asStateFlow()

    private val _isRegistered = MutableStateFlow(true)
    val isRegistered: StateFlow<Boolean> = _isRegistered.asStateFlow()

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

    private var timerJob: Job? = null
    private var telemetryJob: Job? = null
    private var connectionStartTime: Long = 0L

    val formattedDuration: String
        get() {
            val totalSec = _durationSeconds.value
            val m = totalSec / 60
            val s = totalSec % 60
            val h = m / 60
            return if (h > 0) {
                "%02d:%02d:%02d".format(h, m % 60, s)
            } else {
                "%02d:%02d".format(m, s)
            }
        }

    fun toggleConnect() {
        when (_vpnState.value) {
            VpnState.DISCONNECTED -> {
                viewModelScope.launch {
                    _vpnState.value = VpnState.CONNECTING
                    vibrate(40)
                    delay(600) // Fast smooth responsive transition
                    _vpnState.value = VpnState.CONNECTED
                    vibrate(70)
                    connectionStartTime = System.currentTimeMillis()
                    startTelemetry()
                }
            }
            VpnState.CONNECTED, VpnState.CONNECTING -> {
                viewModelScope.launch {
                    _vpnState.value = VpnState.DISCONNECTING
                    vibrate(30)
                    stopTelemetry()
                    delay(300)
                    _vpnState.value = VpnState.DISCONNECTED
                }
            }
            VpnState.DISCONNECTING -> {}
        }
    }

    private fun startTelemetry() {
        timerJob?.cancel()
        _durationSeconds.value = 0L
        timerJob = viewModelScope.launch {
            while (_vpnState.value == VpnState.CONNECTED) {
                delay(1000)
                _durationSeconds.value += 1
            }
        }

        telemetryJob?.cancel()
        telemetryJob = viewModelScope.launch {
            while (_vpnState.value == VpnState.CONNECTED) {
                val baseDown = 62f + Random.nextFloat() * 18f
                val baseUp = 18f + Random.nextFloat() * 8f
                _downloadSpeedMb.value = baseDown
                _uploadSpeedMb.value = baseUp
                _pingMs.value = _selectedServer.value.pingMs + Random.nextInt(-2, 3)
                delay(1500)
            }
        }
    }

    private fun stopTelemetry() {
        val duration = _durationSeconds.value
        val server = _selectedServer.value
        val proto = _protocol.value

        if (duration > 2) {
            viewModelScope.launch {
                try {
                    repository.saveSession(
                        VpnSession(
                            serverName = server.fullName,
                            country = server.country,
                            city = server.city,
                            flagEmoji = server.flagEmoji,
                            protocol = proto.displayName,
                            startTimeMillis = connectionStartTime,
                            durationSeconds = duration,
                            bytesDownloaded = (duration * 45L * 1024L * 1024L),
                            bytesUploaded = (duration * 12L * 1024L * 1024L)
                        )
                    )
                } catch (_: Exception) {}
            }
        }

        timerJob?.cancel()
        timerJob = null
        telemetryJob?.cancel()
        telemetryJob = null
        _downloadSpeedMb.value = 0f
        _uploadSpeedMb.value = 0f
    }

    fun selectServer(server: ServerLocation) {
        _selectedServer.value = server
        _pingMs.value = server.pingMs
    }

    fun setProtocol(protocol: VpnProtocol) {
        _protocol.value = protocol
    }

    fun inspectServer(server: ServerLocation?) {
        _inspectingServer.value = server
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

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        prefs.edit().putBoolean("dark_theme", enabled).apply()
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        prefs.edit().putString("selected_language", language.name).apply()
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
    }

    fun resetOnboarding() {
        _isOnboardingCompleted.value = false
    }

    fun activateKey(key: String) {
        _isActivated.value = true
        _activatedKey.value = key.trim().uppercase()
        prefs.edit().putString("activated_key", key.trim().uppercase()).apply()
        _activationError.value = null
        _showActivationDialog.value = false
    }

    fun deactivateKey() {
        _isActivated.value = false
    }

    fun dismissActivationDialog() {
        _showActivationDialog.value = false
    }

    fun registerUser(name: String, email: String) {
        _username.value = name
        _userEmail.value = email
        prefs.edit().putString("user_username", name).putString("user_email", email).apply()
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                repository.clearHistory()
            } catch (_: Exception) {}
        }
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
        } catch (_: Throwable) {}
    }

    override fun onCleared() {
        super.onCleared()
        stopTelemetry()
    }
}
