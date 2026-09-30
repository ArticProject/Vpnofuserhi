package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ServerLocation
import com.example.model.VpnState
import com.example.ui.theme.VellorEmerald
import com.example.ui.theme.VellorEmeraldGlow

@Composable
fun VellorHeader(
    activeCityCode: String,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onResetOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color.White else Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VL",
                    color = if (isDarkTheme) Color.Black else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = (-0.5).sp
                )
            }
            Text(
                text = "VELLOR",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = if (isDarkTheme) Color.White else Color.Black
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDarkTheme) Color(0xFF18181B) else Color(0xFFF4F4F5))
                    .border(
                        1.dp,
                        if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = activeCityCode,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = if (isDarkTheme) Color.White else Color.Black
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (currentLanguage == AppLanguage.RUSSIAN) "Сменить язык (EN/RU)"
                                else "Change Language (RU/EN)"
                            )
                        },
                        onClick = {
                            showMenu = false
                            if (currentLanguage == AppLanguage.RUSSIAN) {
                                onSelectLanguage(AppLanguage.ENGLISH)
                            } else {
                                onSelectLanguage(AppLanguage.RUSSIAN)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (isDarkTheme) "Светлая тема" else "Тёмная тема"
                            )
                        },
                        onClick = {
                            showMenu = false
                            onToggleDarkTheme(!isDarkTheme)
                        }
                    )
                }
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

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF09090B))
    ) {
        CapitalCityPhotoView(
            server = selectedServer,
            modifier = Modifier.fillMaxSize()
        )

        // Bottom left plate
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 22.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black.copy(alpha = 0.85f))
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedServer.city.uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${selectedServer.flagEmoji} ${selectedServer.country}",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.sp
                )
            }
        }

        // Bottom right dial
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        ) {
            if (isConnecting) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(VellorEmeraldGlow)
                )
            }

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        if (isConnected) VellorEmerald
                        else Color.Black.copy(alpha = 0.90f)
                    )
                    .border(
                        width = 1.2.dp,
                        color = if (isConnected) VellorEmerald else Color.White.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
                    .clickable { onToggleConnect() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Connect",
                    tint = if (isConnected) Color.Black else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun VellorHeadlineSection(
    vpnState: VpnState,
    selectedServer: ServerLocation,
    onToggleConnect: () -> Unit,
    onInspectClick: () -> Unit,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN
    val isConnected = vpnState == VpnState.CONNECTED

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = if (isRu) "НОВЫЙ ПРОТОКОЛ" else "NEW PROTOCOL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = Color(0xFFA1A1AA)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isRu) "Приватность каждый день.\nЧёткий силуэт."
            else "Everyday privacy with\na sharp silhouette.",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isConnected) VellorEmerald else Color(0xFF18181B))
                    .clickable { onToggleConnect() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isConnected) {
                        if (isRu) "● ПОДКЛЮЧЕНО" else "● CONNECTED"
                    } else {
                        if (isRu) "ПОДКЛЮЧИТЬ" else "CONNECT"
                    },
                    color = if (isConnected) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF18181B))
                    .clickable { onInspectClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Inspect",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRu) "ИНФО О НОДЕ" else "INSPECT",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
