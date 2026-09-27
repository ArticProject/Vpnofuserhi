package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
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
import com.example.model.ServerLocation
import com.example.ui.theme.VellorEmerald

/**
 * Headline & Pill Actions Section from Screenshot 1:
 * "NEW COLLECTION"
 * "Everyday clothing with a sharp silhouette." -> "Everyday privacy with a sharp silhouette."
 * "Minimal pieces for women, men and accessories in one clean store."
 * [Open catalog] [Checkout] -> [Open nodes] [Inspect node]
 */
@Composable
fun VellorHeadlineSection(
    onOpenNodesClick: () -> Unit,
    onInspectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "NEW PROTOCOL",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF71717A),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Everyday privacy with a\nsharp silhouette.",
            style = MaterialTheme.typography.displayMedium,
            color = Color(0xFF09090B),
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            letterSpacing = (-1.2).sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Minimal sovereign tunnel for Android, iOS and accessories in one clean interface.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF71717A),
            lineHeight = 22.sp,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Two signature pill buttons (matching Screenshot 1!)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Solid Black Pill: "Open catalog" -> "Open nodes"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(Color(0xFF09090B))
                    .clickable(onClick = onOpenNodesClick)
                    .padding(vertical = 15.dp)
                    .testTag("open_nodes_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Open nodes",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.2.sp
                )
            }

            // White Pill with Thin Border: "Checkout" -> "Inspect node"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E5EA), CircleShape)
                    .clickable(onClick = onInspectClick)
                    .padding(vertical = 15.dp)
                    .testTag("inspect_node_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Inspect node",
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    letterSpacing = 0.2.sp
                )
            }
        }
    }
}

/**
 * 2-Column Grid of Selected Nodes matching Screenshot 2:
 * "FEATURED"
 * "Selected pieces" -> "Selected nodes"
 * "A quick edit from the full catalog." -> "A quick edit from the full network."
 */
@Composable
fun VellorFeaturedNodesSection(
    servers: List<ServerLocation>,
    selectedServer: ServerLocation,
    isConnected: Boolean,
    onSelectServer: (ServerLocation) -> Unit,
    onInspectServer: (ServerLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "FEATURED",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF71717A),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Selected nodes",
            style = MaterialTheme.typography.displayMedium,
            color = Color(0xFF09090B),
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = (-1.0).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "A quick edit from the full network.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF71717A),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 2-Column Node Grid (matching Screenshot 2 cards)
        val chunkedServers = servers.take(6).chunked(2)
        chunkedServers.forEach { rowServers ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowServers.forEach { server ->
                    val isSelected = server.id == selectedServer.id
                    VellorNodeCard(
                        server = server,
                        isSelected = isSelected,
                        isConnected = isConnected && isSelected,
                        onConnect = { onSelectServer(server) },
                        onInspect = { onInspectServer(server) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowServers.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Individual Node Card matching Screenshot 2:
 * - Rounded card container (#FFFFFF or #F4F4F6)
 * - Top-left badge: White pill "Inspect"
 * - Monochrome sculptural illustration
 * - Tag: "SWITZERLAND"
 * - Title: "Zurich Bulldozer Hardware"
 * - Spec pills: "12ms", "10G", "P2P", "TLS" (like shoe sizes "35, 36, 37, 38")
 * - Price & Action: "12 ms" on left, solid black pill "Connect" / "Active" on right (like "Add to cart")
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VellorNodeCard(
    server: ServerLocation,
    isSelected: Boolean,
    isConnected: Boolean,
    onConnect: () -> Unit,
    onInspect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF09090B) else Color(0xFFE5E5EA),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
    ) {
        // Visual Preview Container with "Inspect" badge in top-left
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF4F4F6))
        ) {
            // "Inspect" Pill Badge in top-left (matching screenshot 2!)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(0.8.dp, Color(0xFFE5E5EA), CircleShape)
                    .clickable(onClick = onInspect)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Inspect",
                    color = Color(0xFF09090B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // High-fashion sculptural node emblem
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = server.flagEmoji,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = server.city.uppercase(),
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Country Tag (mirroring "MEN" in screenshot 2)
        Text(
            text = server.country.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF71717A),
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Title (mirroring "Balenciaga Bulldozer Hardware")
        Text(
            text = "${server.city} Bulldozer 01",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF09090B),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Spec badges (mirroring shoe size buttons "35", "36", "37", "38" in screenshot 2!)
        val specBadges = listOf(
            "${server.pingMs}ms",
            "10G",
            if (server.isP2p) "P2P" else "STR",
            "TLS"
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            specBadges.forEach { badge ->
                Box(
                    modifier = Modifier
                        .size(width = 34.dp, height = 24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFF09090B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Row: Ping on left, solid black pill "Connect" / "Active" on right (mirroring "Add to cart")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${server.pingMs} ms",
                color = if (server.pingMs < 30) VellorEmerald else Color(0xFF09090B),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            // Solid Black Pill: "Add to cart" -> "Connect" or "Active"
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isConnected) VellorEmerald else Color(0xFF09090B))
                    .clickable(onClick = onConnect)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("node_connect_${server.id}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isConnected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Text(
                        text = if (isConnected) "Active" else "Connect",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
