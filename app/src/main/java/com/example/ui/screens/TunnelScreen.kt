package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.ServerLocation
import com.example.model.VpnProtocol
import com.example.model.VpnState
import com.example.ui.components.VellorFeaturedNodesSection
import com.example.ui.components.VellorHeader
import com.example.ui.components.VellorHeadlineSection
import com.example.ui.components.VellorHeroCard

@Composable
fun TunnelScreen(
    vpnState: VpnState,
    selectedServer: ServerLocation,
    protocol: VpnProtocol,
    allServers: List<ServerLocation>,
    downloadSpeedMb: Float,
    uploadSpeedMb: Float,
    durationFormatted: String,
    pingMs: Int,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    currentLanguage: AppLanguage,
    selectedLanguage: AppLanguage = currentLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleConnect: () -> Unit,
    onSelectServer: (ServerLocation) -> Unit,
    onOpenServerPicker: () -> Unit,
    onInspectServer: (ServerLocation) -> Unit,
    onResetOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // 1. Top Brand Header: "VL" black circle, "Vellor", "TYO", and hamburger menu with theme & language popup
        item {
            VellorHeader(
                activeCityCode = selectedServer.cityCode,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                currentLanguage = selectedLanguage,
                onSelectLanguage = onSelectLanguage,
                onResetOnboarding = onResetOnboarding
            )
        }

        // 2. Hero Visual Architectural Card with Tokyo Photo, Left Plate & Right Dial
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                VellorHeroCard(
                    vpnState = vpnState,
                    selectedServer = selectedServer,
                    onToggleConnect = onToggleConnect,
                    isRu = currentLanguage == AppLanguage.RUSSIAN
                )
            }
        }

        // 3. Status Section: connection state, selected location, live stats + pills
        item {
            Spacer(modifier = Modifier.height(18.dp))
            VellorHeadlineSection(
                vpnState = vpnState,
                selectedServer = selectedServer,
                onToggleConnect = onToggleConnect,
                onInspectClick = { onInspectServer(selectedServer) },
                durationFormatted = durationFormatted,
                pingMs = pingMs,
                downloadMbps = downloadSpeedMb,
                uploadMbps = uploadSpeedMb,
                currentLanguage = currentLanguage,
                isDarkTheme = isDarkTheme
            )
        }

        // 4. Server cards
        item {
            Spacer(modifier = Modifier.height(26.dp))
            VellorFeaturedNodesSection(
                servers = allServers,
                selectedServer = selectedServer,
                isConnected = isConnected,
                onSelectServer = onSelectServer,
                onInspectServer = onInspectServer,
                currentLanguage = currentLanguage,
                isDarkTheme = isDarkTheme
            )
        }
    }
}
