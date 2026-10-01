package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ServerLocation
import com.example.model.VpnState
import com.example.ui.theme.VellorAmber
import com.example.ui.theme.VellorEmerald
import com.example.ui.theme.VellorRed

@Composable
fun VellorHeadlineSection(
    vpnState: VpnState,
    selectedServer: ServerLocation,
    onToggleConnect: () -> Unit,
    onInspectClick: () -> Unit,
    durationFormatted: String = "00:00",
    pingMs: Int = 0,
    downloadMbps: Float = 0f,
    uploadMbps: Float = 0f,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED
    val isBusy = vpnState == VpnState.CONNECTING || vpnState == VpnState.DISCONNECTING
    val isRu = currentLanguage == AppLanguage.RUSSIAN
    val primary = if (isDarkTheme) Color.White else Color(0xFF09090B)
    val secondary = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
    val statusColor by animateColorAsState(
        targetValue = when {
            isConnected -> VellorEmerald
            isBusy -> VellorAmber
            else -> VellorRed
        },
        animationSpec = spring(stiffness = 300f),
        label = "status_color"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.12f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (vpnState) {
                    VpnState.CONNECTED -> if (isRu) "ЗАЩИЩЕНО" else "PROTECTED"
                    VpnState.CONNECTING -> if (isRu) "ПОДКЛЮЧЕНИЕ…" else "CONNECTING…"
                    VpnState.DISCONNECTING -> if (isRu) "ОТКЛЮЧЕНИЕ…" else "DISCONNECTING…"
                    VpnState.DISCONNECTED -> if (isRu) "НЕ ЗАЩИЩЕНО" else "NOT PROTECTED"
                },
                color = statusColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "${selectedServer.flagEmoji} ${selectedServer.city}",
            style = MaterialTheme.typography.displayMedium,
            color = primary,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            letterSpacing = (-1).sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${selectedServer.country} · VLESS · REALITY",
            style = MaterialTheme.typography.bodyLarge,
            color = secondary,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VellorStatTile(
                label = if (isRu) "ВРЕМЯ" else "TIME",
                value = if (isConnected) durationFormatted else "—",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            VellorStatTile(
                label = if (isRu) "ПИНГ" else "PING",
                value = if (isConnected && pingMs > 0) "$pingMs ms" else "—",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            VellorStatTile(
                label = "↓ / ↑ Mbps",
                value = if (isConnected) "${formatMbps(downloadMbps)} / ${formatMbps(uploadMbps)}" else "—",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1.3f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Two signature pill buttons matching screenshot with bounceClick
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isDarkTheme) Color.White else Color(0xFF09090B))
                    .bounceClick(scaleDown = 0.94f, onClick = onToggleConnect)
                    .testTag("action_toggle_tunnel"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (isConnected) VellorEmerald
                                else if (isDarkTheme) Color(0xFF09090B) else Color.White
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) (if (isRu) "Отключить" else "Disconnect")
                               else (if (isRu) "Подключить туннель" else "Connect Tunnel"),
                        color = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isDarkTheme) Color(0xFF18181B) else Color.White)
                    .border(
                        1.dp,
                        if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE5E5EA),
                        RoundedCornerShape(24.dp)
                    )
                    .bounceClick(scaleDown = 0.94f, onClick = onInspectClick)
                    .testTag("action_inspect_node"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRu) "Сервер ${selectedServer.cityCode}" else "Server ${selectedServer.cityCode}",
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun VellorStatTile(
    label: String,
    value: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F6))
            .border(
                1.dp,
                if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE5E5EA),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = if (isDarkTheme) Color.White else Color(0xFF09090B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatMbps(value: Float): String =
    if (value >= 10f) value.toInt().toString() else String.format(java.util.Locale.US, "%.1f", value)

@Composable
fun VellorFeaturedNodesSection(
    servers: List<ServerLocation>,
    selectedServer: ServerLocation,
    isConnected: Boolean,
    onSelectServer: (ServerLocation) -> Unit,
    onInspectServer: (ServerLocation) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = if (isRu) "СЕРВЕРЫ" else "SERVERS",
            style = MaterialTheme.typography.labelSmall,
            color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isRu) "Доступные локации" else "Available locations",
            style = MaterialTheme.typography.headlineMedium,
            color = if (isDarkTheme) Color.White else Color(0xFF09090B),
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            letterSpacing = (-0.8).sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isRu) "Нажмите на карточку, чтобы сменить сервер." else "Tap a card to switch servers.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        val chunkedServers = servers.chunked(2)
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
                        isRu = isRu,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowServers.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun VellorNodeCard(
    server: ServerLocation,
    isSelected: Boolean,
    isConnected: Boolean,
    onConnect: () -> Unit,
    onInspect: () -> Unit,
    isRu: Boolean = false,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkTheme) Color.White else Color(0xFF09090B)
        } else {
            if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE5E5EA)
        },
        animationSpec = spring(stiffness = 400f),
        label = "node_card_border_col"
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isSelected) 1.6.dp else 1.dp,
        animationSpec = spring(stiffness = 400f),
        label = "node_card_border_w"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDarkTheme) Color(0xFF141416) else Color.White)
            .border(
                width = animatedBorderWidth,
                color = animatedBorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(138.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F0F12))
        ) {
            CapitalCityPhotoView(
                server = server,
                modifier = Modifier.fillMaxSize(),
                darkenFactor = 0.25f,
                showCoordinates = false
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(0.8.dp, Color(0xFFE5E5EA), CircleShape)
                    .bounceClick(scaleDown = 0.90f, onClick = onInspect)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRu) "Инфо" else "Info",
                    color = Color(0xFF09090B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${server.flagEmoji} ${server.city.uppercase()}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = server.city,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = server.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isSelected && isConnected) VellorEmerald.copy(alpha = 0.15f)
                        else if (isDarkTheme) Color(0xFF27272A)
                        else Color(0xFFF4F4F6)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected && isConnected) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(VellorEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (server.pingMs > 0) "${server.pingMs} ms" else "VLESS",
                        color = if (isSelected && isConnected) VellorEmerald
                                else if (isDarkTheme) Color(0xFFA1A1AA)
                                else Color(0xFF71717A),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val btnBg by animateColorAsState(
            targetValue = if (isSelected) {
                if (isDarkTheme) Color.White else Color(0xFF09090B)
            } else {
                if (isDarkTheme) Color(0xFF27272A) else Color(0xFFF4F4F6)
            },
            animationSpec = spring(stiffness = 400f),
            label = "node_btn_bg"
        )
        val btnTextCol by animateColorAsState(
            targetValue = if (isSelected) {
                if (isDarkTheme) Color(0xFF09090B) else Color.White
            } else {
                if (isDarkTheme) Color.White else Color(0xFF09090B)
            },
            animationSpec = spring(stiffness = 400f),
            label = "node_btn_txt"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(btnBg)
                .bounceClick(scaleDown = 0.94f, onClick = onConnect),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    isSelected && isConnected -> if (isRu) "Подключено" else "Connected"
                    isSelected -> if (isRu) "Выбран" else "Selected"
                    else -> if (isRu) "Выбрать" else "Select"
                },
                color = btnTextCol,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
