package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.FilterNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.ui.theme.VellorEmerald

/**
 * iOS Safari Bottom Navigation Bar matching the bottom of Screenshot 1 and 2:
 * - Address search capsule: "vellorshoping.com" / "vellorvpn.com" with lock & reload
 * - System bar: Back, Forward, Share, Bookmarks (Nodes), Tabs (Logs)
 */
@Composable
fun VellorSafariBar(
    currentTab: AppTab,
    isConnected: Boolean,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9FB).copy(alpha = 0.98f))
            .border(
                width = 0.8.dp,
                color = Color(0xFFE5E5EA)
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("vellor_safari_bottom_bar")
    ) {
        // Address Capsule Pill (matching "vellorshoping.com" in screenshots!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFEBEBEF))
                .clickable { onTabSelected(AppTab.TUNNEL) }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Lock icon
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Encrypted SSL",
                    tint = Color(0xFF09090B),
                    modifier = Modifier.size(14.dp)
                )

                // Center: URL domain
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isConnected) "secure.vellorvpn.com" else "vellorvpn.com",
                        color = Color(0xFF09090B),
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }

                // Right: Reload icon
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    tint = Color(0xFF71717A),
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // iOS Navigation Toolbar: Back, Forward, Shield, Nodes, Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onTabSelected(AppTab.TUNNEL) },
                modifier = Modifier.testTag("safari_nav_tunnel")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Tunnel Home",
                    tint = if (currentTab == AppTab.TUNNEL) Color(0xFF007AFF) else Color(0xFF71717A),
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = { onTabSelected(AppTab.NODES) },
                modifier = Modifier.testTag("safari_nav_nodes")
            ) {
                Icon(
                    imageVector = Icons.Filled.Dns,
                    contentDescription = "Nodes Catalog",
                    tint = if (currentTab == AppTab.NODES) Color(0xFF007AFF) else Color(0xFF71717A),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { onTabSelected(AppTab.SHIELD) },
                modifier = Modifier.testTag("safari_nav_shield")
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = "Security Protocols",
                    tint = if (currentTab == AppTab.SHIELD) Color(0xFF007AFF) else Color(0xFF71717A),
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { onTabSelected(AppTab.LOGS) },
                modifier = Modifier.testTag("safari_nav_logs")
            ) {
                Icon(
                    imageVector = Icons.Outlined.FilterNone,
                    contentDescription = "Activity Tabs",
                    tint = if (currentTab == AppTab.LOGS) Color(0xFF007AFF) else Color(0xFF71717A),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
