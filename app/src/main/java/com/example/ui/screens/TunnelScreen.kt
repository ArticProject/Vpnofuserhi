package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ServerLocation
import com.example.model.VpnProtocol
import com.example.model.VpnState
import com.example.ui.components.GlassmorphismSpeedProtocolPanel
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
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onToggleConnect: () -> Unit,
    onSelectServer: (ServerLocation) -> Unit,
    onOpenServerPicker: () -> Unit,
    onInspectServer: (ServerLocation) -> Unit,
    onResetOnboarding: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // 1. Top Brand Header
        item {
            VellorHeader(
                activeCityCode = selectedServer.cityCode,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage,
                onResetOnboarding = onResetOnboarding
            )
        }

        // 2. Architectural Hero Card with Connect Dial
        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                VellorHeroCard(
                    vpnState = vpnState,
                    selectedServer = selectedServer,
                    onToggleConnect = onToggleConnect
                )
            }
        }

        // 3. Quick VLESS Reality Key Copier & Launch for Happ
        if (selectedServer.vlessUrl.isNotBlank()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
                            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = "VLESS",
                                        tint = VellorEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isRu) "VLESS Reality (Happ / v2rayNG)" else "VLESS Reality Direct Key",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color.White else Color.Black
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF27272A))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "100% LIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VellorEmerald
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isRu) "Прямой защищённый ключ сервера Германии (YouTube, Instagram без ограничений):"
                                else "Direct German server tunnel key with Reality TLS camouflaging:",
                                fontSize = 11.sp,
                                color = Color(0xFFA1A1AA)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDarkTheme) Color.White else Color.Black)
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Vellor VLESS", selectedServer.vlessUrl)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(
                                                context,
                                                if (isRu) "✓ VLESS ключ скопирован! Вставьте в Happ" else "✓ VLESS key copied to clipboard!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = if (isDarkTheme) Color.Black else Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isRu) "Скопировать ключ" else "Copy Key",
                                            color = if (isDarkTheme) Color.Black else Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF27272A))
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedServer.vlessUrl))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Vellor VLESS", selectedServer.vlessUrl))
                                                Toast.makeText(
                                                    context,
                                                    if (isRu) "Ключ скопирован в буфер обмена" else "Key copied to clipboard",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Launch,
                                            contentDescription = "Open Happ",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isRu) "Открыть в Happ" else "Open in Happ",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Live Speed & Duration Telemetry Panel
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                GlassmorphismSpeedProtocolPanel(
                    downloadSpeedMb = downloadSpeedMb,
                    uploadSpeedMb = uploadSpeedMb,
                    durationFormatted = durationFormatted,
                    pingMs = pingMs,
                    protocol = protocol,
                    isConnected = vpnState == VpnState.CONNECTED,
                    isDarkTheme = isDarkTheme,
                    currentLanguage = currentLanguage
                )
            }
        }

        // 5. Headline Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            VellorHeadlineSection(
                vpnState = vpnState,
                selectedServer = selectedServer,
                onToggleConnect = onToggleConnect,
                onInspectClick = { onInspectServer(selectedServer) },
                currentLanguage = currentLanguage
            )
        }

        // 6. Featured Nodes List
        item {
            Spacer(modifier = Modifier.height(18.dp))
            VellorFeaturedNodesSection(
                servers = allServers,
                selectedServer = selectedServer,
                onSelectServer = onSelectServer,
                onSeeAll = onOpenServerPicker,
                isDarkTheme = isDarkTheme,
                currentLanguage = currentLanguage
            )
        }
    }
}
