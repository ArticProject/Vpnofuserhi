package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppTab
import com.example.model.VpnState
import com.example.ui.components.MeshNetworkBackground
import com.example.ui.components.VellorSafariBar
import com.example.viewmodel.VpnViewModel

@Composable
fun VellorApp(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()

    if (!isOnboardingCompleted) {
        OnboardingScreen(
            isDarkTheme = isDarkTheme,
            onToggleDarkTheme = { viewModel.setDarkTheme(it) },
            currentLanguage = selectedLanguage,
            onSelectLanguage = { viewModel.setLanguage(it) },
            onFinishOnboarding = { viewModel.completeOnboarding() },
            modifier = modifier
        )
        return
    }

    var currentTab by remember { mutableStateOf(AppTab.TUNNEL) }

    val vpnState by viewModel.vpnState.collectAsStateWithLifecycle()
    val selectedServer by viewModel.selectedServer.collectAsStateWithLifecycle()
    val protocol by viewModel.protocol.collectAsStateWithLifecycle()
    val servers by viewModel.servers.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()

    val downloadSpeed by viewModel.downloadSpeedMb.collectAsStateWithLifecycle()
    val uploadSpeed by viewModel.uploadSpeedMb.collectAsStateWithLifecycle()
    val durationSec by viewModel.durationSeconds.collectAsStateWithLifecycle()
    val pingMs by viewModel.pingMs.collectAsStateWithLifecycle()

    val killSwitch by viewModel.killSwitch.collectAsStateWithLifecycle()
    val stealth by viewModel.stealth.collectAsStateWithLifecycle()
    val doubleHop by viewModel.doubleHop.collectAsStateWithLifecycle()
    val adBlock by viewModel.adBlock.collectAsStateWithLifecycle()
    val inspectingServer by viewModel.inspectingServer.collectAsStateWithLifecycle()

    val username by viewModel.username.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val sovereignId by viewModel.sovereignId.collectAsStateWithLifecycle()
    val isRegistered by viewModel.isRegistered.collectAsStateWithLifecycle()
    val isActivated by viewModel.isActivated.collectAsStateWithLifecycle()
    val activatedKey by viewModel.activatedKey.collectAsStateWithLifecycle()
    val showActivationDialog by viewModel.showActivationDialog.collectAsStateWithLifecycle()
    val activationError by viewModel.activationError.collectAsStateWithLifecycle()

    if (currentTab != AppTab.TUNNEL) {
        BackHandler {
            currentTab = AppTab.TUNNEL
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color(0xFF09090B) else Color.White)
    ) {
        MeshNetworkBackground(
            modifier = Modifier.fillMaxSize(),
            isDarkTheme = isDarkTheme,
            isConnected = vpnState == VpnState.CONNECTED
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_switch"
            ) { tab ->
                when (tab) {
                    AppTab.TUNNEL -> {
                        TunnelScreen(
                            vpnState = vpnState,
                            selectedServer = selectedServer,
                            protocol = protocol,
                            allServers = servers,
                            downloadSpeedMb = downloadSpeed,
                            uploadSpeedMb = uploadSpeed,
                            durationFormatted = viewModel.formattedDuration,
                            pingMs = pingMs,
                            isDarkTheme = isDarkTheme,
                            onToggleDarkTheme = { viewModel.setDarkTheme(it) },
                            currentLanguage = selectedLanguage,
                            onSelectLanguage = { viewModel.setLanguage(it) },
                            onToggleConnect = { viewModel.toggleConnect() },
                            onSelectServer = { server -> viewModel.selectServer(server) },
                            onOpenServerPicker = { currentTab = AppTab.NODES },
                            onInspectServer = { server -> viewModel.inspectServer(server) },
                            onResetOnboarding = { viewModel.resetOnboarding() }
                        )
                    }
                    AppTab.NODES -> {
                        LocationsScreen(
                            servers = servers,
                            selectedServer = selectedServer,
                            isConnected = vpnState == VpnState.CONNECTED,
                            isDarkTheme = isDarkTheme,
                            currentLanguage = selectedLanguage,
                            onSelectServer = { server ->
                                viewModel.selectServer(server)
                                currentTab = AppTab.TUNNEL
                            },
                            onToggleFavorite = { serverId -> viewModel.toggleFavorite(serverId) },
                            onBack = { currentTab = AppTab.TUNNEL }
                        )
                    }
                    AppTab.SHIELD -> {
                        SecurityScreen(
                            currentProtocol = protocol,
                            killSwitch = killSwitch,
                            stealth = stealth,
                            doubleHop = doubleHop,
                            adBlock = adBlock,
                            onSelectProtocol = { proto -> viewModel.setProtocol(proto) },
                            onToggleKillSwitch = { viewModel.toggleKillSwitch() },
                            onToggleStealth = { viewModel.toggleStealth() },
                            onToggleDoubleHop = { viewModel.toggleDoubleHop() },
                            onToggleAdBlock = { viewModel.toggleAdBlock() },
                            isDarkTheme = isDarkTheme,
                            currentLanguage = selectedLanguage
                        )
                    }
                    AppTab.PROFILE -> {
                        ProfileScreen(
                            username = username,
                            userEmail = userEmail,
                            sovereignId = sovereignId,
                            isRegistered = isRegistered,
                            isActivated = isActivated,
                            activatedKey = activatedKey,
                            activationError = activationError,
                            sessions = sessions,
                            onActivateKey = { key -> viewModel.activateKey(key) },
                            onDeactivateKey = { viewModel.deactivateKey() },
                            onRegisterUser = { name, email -> viewModel.registerUser(name, email) },
                            onClearHistory = { viewModel.clearHistory() },
                            isDarkTheme = isDarkTheme,
                            currentLanguage = selectedLanguage
                        )
                    }
                }
            }

            VellorSafariBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
                isDarkTheme = isDarkTheme,
                currentLanguage = selectedLanguage,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            inspectingServer?.let { server ->
                ServerInspectorDialog(
                    server = server,
                    isConnected = vpnState == VpnState.CONNECTED && server.id == selectedServer.id,
                    onDismiss = { viewModel.inspectServer(null) },
                    onConnect = {
                        viewModel.selectServer(server)
                        if (vpnState != VpnState.CONNECTED) {
                            viewModel.toggleConnect()
                        }
                    },
                    isDarkTheme = isDarkTheme,
                    currentLanguage = selectedLanguage
                )
            }

            if (showActivationDialog) {
                ActivationRequiredDialog(
                    onDismissRequest = { viewModel.dismissActivationDialog() },
                    onActivateKey = { key -> viewModel.activateKey(key) },
                    onNavigateToProfile = {
                        viewModel.dismissActivationDialog()
                        currentTab = AppTab.PROFILE
                    },
                    activationError = activationError,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}
