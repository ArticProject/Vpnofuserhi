package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnState
import com.example.ui.theme.VellorEmerald

/**
 * Top announcement banner matching screenshot 1:
 * "20% OFF YOUR FIRST ORDER — APPLIED AUTOMATICALLY ✕"
 * In our VPN: "ZERO-LOGS SOVEREIGN TUNNEL — ACTIVE ENCRYPTION ✕"
 */
@Composable
fun VellorAnnouncementBar(
    vpnState: VpnState,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(visible = isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFF09090B))
                .padding(horizontal = 16.dp, vertical = 7.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (vpnState) {
                        VpnState.CONNECTED -> "TUNNEL ACTIVE — ZERO LOGS RECORDED"
                        VpnState.CONNECTING -> "ESTABLISHING CHACHA20 ENCRYPTED TUNNEL..."
                        VpnState.DISCONNECTING -> "CLOSING TUNNEL INTERFACES..."
                        VpnState.DISCONNECTED -> "10Gbps RAM-ONLY FLEET — APPLIED AUTOMATICALLY"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.1.sp,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { isVisible = false }
                )
            }
        }
    }
}

/**
 * Header matching Screenshot 1:
 * Left: "VL" circle badge + "Vellor" title
 * Right: "2" circle badge + "☰" menu icon
 */
@Composable
fun VellorHeader(
    activeNodeCount: Int = 2,
    isConnected: Boolean = false,
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: VL Circle Badge + Brand Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onMenuClick)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF09090B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VL",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Vellor",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF09090B),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.5).sp
            )
        }

        // Right: "2" or status count badge + "☰" menu
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) VellorEmerald else Color(0xFF09090B)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isConnected) "1" else "$activeNodeCount",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                    .clickable(onClick = onMenuClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFF09090B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Architectural Hero Card matching Screenshot 1:
 * Rounded card, monochrome editorial architectural visual,
 * "New season / OUTERWEAR, KNITWEAR, DENIM, BAGS."
 * Center Translucent Circular Play / Connect Button!
 */
@Composable
fun VellorHeroCard(
    vpnState: VpnState,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED
    val isConnecting = vpnState == VpnState.CONNECTING || vpnState == VpnState.DISCONNECTING

    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hero_pulse"
    )

    val spinnerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hero_spin"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF18181B))
    ) {
        // Architectural Monochrome Background (Custom Canvas lines replicating urban bridge/monolith in screenshot 1)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Moody charcoal gradient background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF27272A),
                        Color(0xFF18181B),
                        Color(0xFF09090B)
                    )
                )
            )

            // Structural architectural lines in 10% white (resembling high-fashion balustrade / building silhouette)
            val strokeColor = Color.White.copy(alpha = 0.08f)
            for (i in 0..12) {
                val x = w * (i / 12f)
                drawLine(
                    color = strokeColor,
                    start = Offset(x, h * 0.45f),
                    end = Offset(x, h),
                    strokeWidth = 1.5f
                )
            }
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(0f, h * 0.45f),
                end = Offset(w, h * 0.45f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.12f),
                start = Offset(0f, h * 0.60f),
                end = Offset(w, h * 0.60f),
                strokeWidth = 1.5f
            )
        }

        // Overlay Content: "New season / WIREGUARD, CHACHA20, DUAL-HOP, STEALTH."
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 24.dp)
                .fillMaxWidth(0.75f)
                .background(
                    color = Color.Black.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = if (isConnected) "Encrypted Active" else "New protocol",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "WIREGUARD,\nCHACHA20, DENIM,\nNODES.",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        // Center Big Circular Translucent Play / Connect Button (exactly like play button in Screenshot 1!)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(1.5.dp, VellorEmerald.copy(alpha = 0.6f), CircleShape)
                )
            }

            if (isConnecting) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .rotate(spinnerRotation)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(Color.Transparent, Color.White)),
                            CircleShape
                        )
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.28f))
                    .border(1.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                .clickable(onClick = onToggleConnect)
                .testTag("hero_play_connect_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (vpnState) {
                        VpnState.CONNECTED -> Icons.Filled.Lock
                        VpnState.CONNECTING, VpnState.DISCONNECTING -> Icons.Filled.PowerSettingsNew
                        VpnState.DISCONNECTED -> Icons.Filled.PlayArrow
                    },
                    contentDescription = "Connect",
                    tint = if (isConnected) VellorEmerald else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
