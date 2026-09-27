package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
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
    var currentTab by remember { mutableStateOf(AppTab.TUNNEL) }

    val vpnState by viewModel.vpnState.collectAsStateWithLifecycle()
    val selectedServer by viewModel.selectedServer.collectAsStateWithLifecycle()
    val protocol by viewModel.protocol.collectAsStateWithLifecycle()
    val servers by viewModel.servers.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()

    val downloadSpeed by viewModel.downloadSpeedMb.collectAsStateWithLifecycle()
    val uploadSpeed by viewModel.uploadSpeedMb.collectAsStateWithLifecycle()
    val speedHistory by viewModel.speedHistory.collectAsStateWithLifecycle()
    val durationSec by viewModel.durationSeconds.collectAsStateWithLifecycle()
    val pingMs by viewModel.pingMs.collectAsStateWithLifecycle()

    val killSwitch by viewModel.killSwitch.collectAsStateWithLifecycle()
    val stealth by viewModel.stealth.collectAsStateWithLifecycle()
    val doubleHop by viewModel.doubleHop.collectAsStateWithLifecycle()
    val adBlock by viewModel.adBlock.collectAsStateWithLifecycle()
    val inspectingServer by viewModel.inspectingServer.collectAsStateWithLifecycle()

    // Handle Back button to return to Main Feed
    if (currentTab != AppTab.TUNNEL) {
        BackHandler {
            currentTab = AppTab.TUNNEL
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Delicate geometric constellation wireframe on pure white (matching Screenshots 1 & 2)
        MeshNetworkBackground(
            modifier = Modifier.fillMaxSize(),
            isConnected = vpnState == VpnState.CONNECTED
        )

        // Screen Content with status bar insets
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_switch_transition"
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
                            onToggleConnect = { viewModel.toggleConnect() },
                            onSelectServer = { server ->
                                viewModel.selectServer(server)
                            },
                            onOpenServerPicker = { currentTab = AppTab.NODES },
                            onInspectServer = { server -> viewModel.inspectServer(server) },
                            onMenuClick = { currentTab = AppTab.SHIELD }
                        )
                    }

                    AppTab.NODES -> {
                        LocationsScreen(
                            servers = servers,
                            selectedServer = selectedServer,
                            onSelectServer = { server ->
                                viewModel.selectServer(server)
                                currentTab = AppTab.TUNNEL
                            },
                            onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                            onInspectServer = { server -> viewModel.inspectServer(server) }
                        )
                    }

                    AppTab.SHIELD -> {
                        SecurityScreen(
                            currentProtocol = protocol,
                            onProtocolSelected = { viewModel.setProtocol(it) },
                            killSwitchEnabled = killSwitch,
                            onToggleKillSwitch = { viewModel.toggleKillSwitch(it) },
                            stealthEnabled = stealth,
                            onToggleStealth = { viewModel.toggleStealth(it) },
                            doubleHopEnabled = doubleHop,
                            onToggleDoubleHop = { viewModel.toggleDoubleHop(it) },
                            adBlockEnabled = adBlock,
                            onToggleAdBlock = { viewModel.toggleAdBlock(it) }
                        )
                    }

                    AppTab.LOGS -> {
                        ActivityScreen(
                            sessions = sessions,
                            onClearHistory = { viewModel.clearHistory() }
                        )
                    }
                }
            }
        }

        // iOS Safari Bottom Navigation Bar matching Screenshot 1 & 2!
        VellorSafariBar(
            currentTab = currentTab,
            isConnected = vpnState == VpnState.CONNECTED,
            onTabSelected = { currentTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Inspector Modal Sheet (White luxury card)
        inspectingServer?.let { server ->
            ServerInspectorDialog(
                server = server,
                protocol = protocol,
                onDismiss = { viewModel.inspectServer(null) }
            )
        }
    }
}
