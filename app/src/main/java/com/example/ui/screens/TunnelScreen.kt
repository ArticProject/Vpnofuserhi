package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerLocation
import com.example.model.VpnProtocol
import com.example.model.VpnState
import com.example.ui.components.VellorAnnouncementBar
import com.example.ui.components.VellorCategoryBanners
import com.example.ui.components.VellorFeaturedNodesSection
import com.example.ui.components.VellorHeader
import com.example.ui.components.VellorHeadlineSection
import com.example.ui.components.VellorHeroCard
import com.example.ui.theme.VellorEmerald

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
    onToggleConnect: () -> Unit,
    onSelectServer: (ServerLocation) -> Unit,
    onOpenServerPicker: () -> Unit,
    onInspectServer: (ServerLocation) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // 1. Black Announcement Bar across top (matching Screenshot 1)
        item {
            VellorAnnouncementBar(vpnState = vpnState)
        }

        // 2. Top Brand Header: "VL" + "Vellor" + Active City Code + "☰"
        item {
            VellorHeader(
                activeCityCode = selectedServer.cityCode,
                isConnected = isConnected,
                onMenuClick = onMenuClick
            )
        }

        // 3. Hero Visual Architectural Card with Center Dial (matching Screenshot 1)
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                VellorHeroCard(
                    vpnState = vpnState,
                    selectedServer = selectedServer,
                    onToggleConnect = onToggleConnect
                )
            }
        }

        // 4. Headline Section: "NEW PROTOCOL" + "Everyday privacy with a sharp silhouette." + Pills
        item {
            Spacer(modifier = Modifier.height(18.dp))
            VellorHeadlineSection(
                vpnState = vpnState,
                selectedServer = selectedServer,
                onToggleConnect = onToggleConnect,
                onInspectClick = { onInspectServer(selectedServer) }
            )
        }

        // 5. Category Banners (Women, Men, Accessories matching the video!)
        item {
            Spacer(modifier = Modifier.height(26.dp))
            VellorCategoryBanners(
                onCategoryClick = { onOpenServerPicker() }
            )
        }

        // 6. "FEATURED" -> "Selected nodes" 2-Column Product Grid (matching Screenshot 2 & video!)
        item {
            Spacer(modifier = Modifier.height(32.dp))
            VellorFeaturedNodesSection(
                servers = allServers,
                selectedServer = selectedServer,
                isConnected = isConnected,
                onSelectServer = onSelectServer,
                onInspectServer = onInspectServer
            )
        }
    }
}
