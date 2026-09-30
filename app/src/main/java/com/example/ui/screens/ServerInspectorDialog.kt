package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.model.ServerLocation
import com.example.ui.theme.VellorEmerald

@Composable
fun ServerInspectorDialog(
    server: ServerLocation,
    isConnected: Boolean,
    onDismiss: () -> Unit,
    onConnect: () -> Unit,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isDarkTheme) Color(0xFF141416) else Color.White)
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${server.flagEmoji} ${server.city}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                        Text(
                            text = server.country,
                            fontSize = 12.sp,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF27272A))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${server.pingMs} ms",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VellorEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                InspectorRow(title = "IP Address", value = server.ipAddress, isDark = isDarkTheme)
                InspectorRow(title = "Load", value = "${server.loadPercent}%", isDark = isDarkTheme)
                InspectorRow(title = "P2P Torrent", value = if (server.isP2p) "Supported" else "Blocked", isDark = isDarkTheme)
                InspectorRow(title = "Streaming", value = if (server.isStreaming) "Optimized (4K HDR)" else "Standard", isDark = isDarkTheme)

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = if (isRu) "Закрыть" else "Close",
                            color = Color(0xFFA1A1AA)
                        )
                    }
                    Button(
                        onClick = {
                            onConnect()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkTheme) Color.White else Color.Black)
                    ) {
                        Text(
                            text = if (isConnected) (if (isRu) "Выбрать ноду" else "Selected")
                            else (if (isRu) "Подключить" else "Connect"),
                            color = if (isDarkTheme) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorRow(title: String, value: String, isDark: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 12.sp, color = Color(0xFFA1A1AA))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else Color.Black
        )
    }
}
