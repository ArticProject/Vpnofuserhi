package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ServerLocation
import com.example.model.VpnState
import com.example.ui.theme.VellorEmerald

@Composable
fun VellorHeader(
    activeCityCode: String = "FRA",
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: (Boolean) -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onResetOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Uncompleted Infinity Icon (Meta-style) + Vellor App Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { menuExpanded = true }
        ) {
            OpenInfinityLogo(
                size = 28.dp,
                color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                strokeWidth = 3.2f
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Vellor",
                style = MaterialTheme.typography.titleLarge,
                color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = (-0.5).sp
            )
        }

        // Right: Active city code badge + hamburger menu (with dropdown)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color.White else Color(0xFF09090B))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeCityCode,
                    color = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDarkTheme) Color(0xFF18181B) else Color.White)
                        .border(
                            1.dp,
                            if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE5E5EA),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { menuExpanded = !menuExpanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "Preferences Menu",
                        tint = if (isDarkTheme) Color.White else Color(0xFF09090B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Dropdown popup for Theme and Language
                VellorPreferencesMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    isDarkTheme = isDarkTheme,
                    onToggleDarkTheme = onToggleDarkTheme,
                    currentLanguage = currentLanguage,
                    onSelectLanguage = onSelectLanguage,
                    onResetOnboarding = onResetOnboarding
                )
            }
        }
    }
}

@Composable
fun VellorHeroCard(
    vpnState: VpnState,
    selectedServer: ServerLocation,
    onToggleConnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED
    val isConnecting = vpnState == VpnState.CONNECTING || vpnState == VpnState.DISCONNECTING

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF09090B))
    ) {
        // High-resolution architectural photo (Tokyo / Capital city)
        CapitalCityPhotoView(
            server = selectedServer,
            modifier = Modifier.fillMaxSize(),
            darkenFactor = 0.35f
        )

        // Vignette gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        // Overlay Card on the left: Country and Capital plate
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 22.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(0.8.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) VellorEmerald else Color.White)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "${selectedServer.country.uppercase()} · ${selectedServer.city.uppercase()}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "NATIONAL CAPITAL\nWIREGUARD 10G\nSECURE GATEWAY",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 17.sp,
                    letterSpacing = (-0.2).sp
                )
            }
        }

        // Connection Dial on the right
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ethereal harmonic multi-layer pulse rings when Connected
            HarmonicPulseRings(
                baseSize = 96.dp,
                ringColor = Color.White,
                active = isConnected
            )

            // High-precision rotating cyber ring only when Linking
            ConnectingSpinner(isConnecting = isConnecting)

            // Outer dark ring with animated border
            val outerBorderColor by animateColorAsState(
                targetValue = if (isConnected) Color.White else Color.White.copy(alpha = 0.6f),
                animationSpec = spring(stiffness = 300f),
                label = "dial_outer_border"
            )

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.70f))
                    .border(2.dp, outerBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val buttonBg by animateColorAsState(
                    targetValue = if (isConnected) Color.White else Color(0xFF09090B),
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = 350f),
                    label = "dial_btn_bg"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isConnected) Color(0xFF09090B) else Color.White,
                    animationSpec = spring(stiffness = 350f),
                    label = "dial_icon_tint"
                )

                // Inner button with tactile bounce compression on tap
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(buttonBg)
                        .bounceClick(scaleDown = 0.91f, onClick = onToggleConnect)
                        .testTag("hero_power_dial"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AnimatedContent(
                            targetState = vpnState,
                            transitionSpec = {
                                (fadeIn(animationSpec = spring(stiffness = 500f)) + scaleIn(initialScale = 0.8f, animationSpec = spring(stiffness = 500f)))
                                    .togetherWith(fadeOut(animationSpec = spring(stiffness = 500f)) + scaleOut(targetScale = 0.8f, animationSpec = spring(stiffness = 500f)))
                            },
                            label = "dial_icon_anim"
                        ) { targetState ->
                            Icon(
                                imageVector = when (targetState) {
                                    VpnState.CONNECTED -> Icons.Filled.Lock
                                    VpnState.CONNECTING, VpnState.DISCONNECTING -> Icons.Filled.PowerSettingsNew
                                    VpnState.DISCONNECTED -> Icons.Filled.PowerSettingsNew
                                    else -> Icons.Filled.PowerSettingsNew
                                },
                                contentDescription = "Toggle Connection",
                                tint = iconTint,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = when (vpnState) {
                                VpnState.CONNECTED -> "SECURE"
                                VpnState.CONNECTING, VpnState.DISCONNECTING -> "LINKING"
                                VpnState.DISCONNECTED -> "CONNECT"
                            },
                            color = iconTint,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectingSpinner(isConnecting: Boolean) {
    if (!isConnecting) return
    val infiniteTransition = rememberInfiniteTransition(label = "hero_spin")
    val spinnerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hero_spin_rot"
    )
    Box(
        modifier = Modifier
            .size(108.dp)
            .graphicsLayer { rotationZ = spinnerRotation }
            .clip(CircleShape)
            .border(
                2.5.dp,
                Brush.sweepGradient(listOf(Color.Transparent, Color.White, Color.Transparent)),
                CircleShape
            )
    )
}

