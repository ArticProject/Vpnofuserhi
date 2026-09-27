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
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
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
import com.example.model.ServerLocation
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
                        VpnState.CONNECTED -> "TUNNEL ACTIVE — ALL TRAFFIC ROUTED & ENCRYPTED"
                        VpnState.CONNECTING -> "ESTABLISHING CHACHA20 KEY EXCHANGE..."
                        VpnState.DISCONNECTING -> "CLOSING TUNNEL INTERFACES..."
                        VpnState.DISCONNECTED -> "HIGH SPEED 10Gbps FLEET — APPLIED AUTOMATICALLY"
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
 * Right: Active server city code (e.g. "ZRH") + "☰" menu icon
 */
@Composable
fun VellorHeader(
    activeCityCode: String = "ZRH",
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

        // Right: Active city code badge (e.g. "ZRH" or active count) + "☰" menu
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF09090B))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeCityCode,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
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
 * Architectural Hero Card with High-Fashion Minimalist Dial Connection Control:
 * Replaces the generic button with a sophisticated luxury dial and state machine.
 */
@Composable
fun VellorHeroCard(
    vpnState: VpnState,
    selectedServer: ServerLocation,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED
    val isConnecting = vpnState == VpnState.CONNECTING || vpnState == VpnState.DISCONNECTING

    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
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
            .height(280.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF09090B))
    ) {
        // High-Resolution Architectural Capital City Photo with moody Scrim
        CapitalCityPhotoView(
            server = selectedServer,
            modifier = Modifier.fillMaxSize(),
            darkenFactor = 0.45f,
            showCoordinates = true
        )

        // Overlay Content: "New protocol / WIREGUARD, CHACHA20, DENIM, NODES."
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
                .fillMaxWidth(0.72f)
                .background(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = "${selectedServer.city.uppercase()} · ${selectedServer.country.uppercase()}",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "WIREGUARD,\nCHACHA20, DENIM,\nNODES.",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 19.sp,
                    letterSpacing = (-0.3).sp
                )
            }
        }

        // Redesigned Luxury Tactile Power Control Dial (in the center!)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            // Concentric halo when Connected
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                )
            }

            // High-precision rotating hairline ring when Connecting
            if (isConnecting) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .rotate(spinnerRotation)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(Color.Transparent, Color.White)),
                            CircleShape
                        )
                )
            }

            // Tactile Luxury Monochrome Core Dial
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (isConnected) Color.White
                        else Color.Black.copy(alpha = 0.65f)
                    )
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.85f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onToggleConnect)
                    .testTag("hero_power_dial"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (vpnState) {
                            VpnState.CONNECTED -> Icons.Filled.Lock
                            VpnState.CONNECTING, VpnState.DISCONNECTING -> Icons.Filled.PowerSettingsNew
                            VpnState.DISCONNECTED -> Icons.Filled.PowerSettingsNew
                        },
                        contentDescription = "Toggle Connection",
                        tint = if (isConnected) Color(0xFF09090B) else Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = when (vpnState) {
                            VpnState.CONNECTED -> "SECURE"
                            VpnState.CONNECTING, VpnState.DISCONNECTING -> "LINKING"
                            VpnState.DISCONNECTED -> "CONNECT"
                        },
                        color = if (isConnected) Color(0xFF09090B) else Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

/**
 * Editorial Category Banners matching vellorshoping.com in the video:
 * 3 full-width horizontal luxury banners with pure black-and-white photography:
 * 1. "Women / WOMEN / Coats, dresses, knitwear"
 * 2. "Men / MEN / Overshirts, denim, tailoring"
 * 3. "Accessories / ACCESSORIES / Shoes, bags, caps"
 */
@Composable
fun VellorCategoryBanners(
    onCategoryClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditorialBannerItem(
            category = "Women",
            title = "WOMEN",
            subtitle = "Coats, dresses, knitwear",
            imageUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?auto=format&fit=crop&w=800&q=80",
            onClick = { onCategoryClick("Women") }
        )

        EditorialBannerItem(
            category = "Men",
            title = "MEN",
            subtitle = "Overshirts, denim, tailoring",
            imageUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=800&q=80",
            onClick = { onCategoryClick("Men") }
        )

        EditorialBannerItem(
            category = "Accessories",
            title = "ACCESSORIES",
            subtitle = "Shoes, bags, caps",
            imageUrl = "https://images.unsplash.com/photo-1551488831-00ddcb6c6bd3?auto=format&fit=crop&w=800&q=80",
            onClick = { onCategoryClick("Accessories") }
        )
    }
}

@Composable
private fun EditorialBannerItem(
    category: String,
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: () -> Unit
) {
    val monochromeMatrix = remember { androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) } }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF09090B))
            .clickable(onClick = onClick)
    ) {
        // Black and white high fashion photograph via Coil
        coil.compose.AsyncImage(
            model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.colorMatrix(monochromeMatrix),
            modifier = Modifier.fillMaxSize()
        )

        // Contrast scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // Typography matching the video
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = category,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

