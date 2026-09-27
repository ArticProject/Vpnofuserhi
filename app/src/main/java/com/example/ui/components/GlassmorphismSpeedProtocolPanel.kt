package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnProtocol
import com.example.model.VpnState
import com.example.ui.theme.VellorEmerald

/**
 * High-Fashion Glassmorphism Information Panel:
 * Displays real-time download/upload speed and active cryptographic tunnel protocol.
 */
@Composable
fun GlassmorphismSpeedProtocolPanel(
    downloadSpeedMb: Float,
    uploadSpeedMb: Float,
    protocol: VpnProtocol,
    vpnState: VpnState,
    pingMs: Int,
    onProtocolClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED

    val infiniteTransition = rememberInfiniteTransition(label = "glass_pulse")
    val liveDotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    // Glassmorphic Outer Container
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.88f),
                        Color(0xFFF9F9FB).copy(alpha = 0.76f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFFE5E5EA).copy(alpha = 0.65f),
                        Color.White.copy(alpha = 0.45f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 400f)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
            .testTag("glassmorphism_telemetry_panel")
    ) {
        Column {
            // Header Row: Section Label + Live Status Light
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (isConnected) VellorEmerald.copy(alpha = liveDotAlpha)
                                else Color(0xFF71717A).copy(alpha = 0.6f)
                            )
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = if (isConnected) "TUNNEL TELEMETRY · 10Gbps FLEET" else "TUNNEL TELEMETRY · STANDBY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF71717A),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontSize = 10.sp
                    )
                }

                // Latency Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFEFEFF2).copy(alpha = 0.9f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isConnected) "$pingMs ms" else "--- ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isConnected && pingMs < 30) VellorEmerald else Color(0xFF09090B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Speed Meters: Download and Upload
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Download Metric Glass Pill
                SpeedMetricGlassCard(
                    modifier = Modifier.weight(1f),
                    title = "DOWNLINK",
                    speedValue = if (isConnected) String.format("%.1f", downloadSpeedMb) else "0.0",
                    unit = "MB/s",
                    isDownload = true,
                    isActive = isConnected
                )

                // Upload Metric Glass Pill
                SpeedMetricGlassCard(
                    modifier = Modifier.weight(1f),
                    title = "UPLINK",
                    speedValue = if (isConnected) String.format("%.1f", uploadSpeedMb) else "0.0",
                    unit = "MB/s",
                    isDownload = false,
                    isActive = isConnected
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider Hairline with Glass Accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE5E5EA).copy(alpha = 0.7f))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Active Cryptographic Protocol Sub-Panel (Interactive)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF1F1F4).copy(alpha = 0.6f))
                    .border(0.8.dp, Color(0xFFE5E5EA).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .clickable(onClick = onProtocolClick)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Protocol Shield Icon
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF09090B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Active Protocol",
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = protocol.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF09090B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF09090B))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = protocol.badge,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${protocol.cipher} · PFS Zero-Logs",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF71717A),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }

                // Change / Chevron indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Protocols",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF71717A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Change Protocol",
                        tint = Color(0xFF71717A),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedMetricGlassCard(
    title: String,
    speedValue: String,
    unit: String,
    isDownload: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.72f))
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5EA).copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF71717A),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) {
                                if (isDownload) VellorEmerald.copy(alpha = 0.12f)
                                else Color(0xFF09090B).copy(alpha = 0.08f)
                            } else Color(0xFFE5E5EA).copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDownload) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                        contentDescription = null,
                        tint = if (isActive) {
                            if (isDownload) VellorEmerald else Color(0xFF09090B)
                        } else Color(0xFF71717A),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = speedValue,
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    color = Color(0xFF71717A),
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}
