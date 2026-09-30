package com.example.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.VpnProtocol
import com.example.ui.theme.VellorEmerald

@Composable
fun GlassmorphismSpeedProtocolPanel(
    downloadSpeedMb: Float,
    uploadSpeedMb: Float,
    durationFormatted: String,
    pingMs: Int,
    protocol: VpnProtocol,
    isConnected: Boolean,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
            .border(
                width = 1.dp,
                color = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) VellorEmerald else Color(0xFFA1A1AA))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isConnected) {
                            if (isRu) "АКТИВНАЯ СЕССИЯ" else "LIVE ENCRYPTED TUNNEL"
                        } else {
                            if (isRu) "РЕЖИМ ОЖИДАНИЯ" else "STANDBY READY"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isConnected) VellorEmerald else Color(0xFFA1A1AA)
                    )
                }

                Text(
                    text = durationFormatted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Download
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Download",
                        tint = VellorEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = if (isRu) "Входящий" else "Down",
                            fontSize = 10.sp,
                            color = Color(0xFFA1A1AA)
                        )
                        Text(
                            text = if (isConnected) "%.1f MB/s".format(downloadSpeedMb) else "0.0 MB/s",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                }

                // Upload
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Upload",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = if (isRu) "Исходящий" else "Up",
                            fontSize = 10.sp,
                            color = Color(0xFFA1A1AA)
                        )
                        Text(
                            text = if (isConnected) "%.1f MB/s".format(uploadSpeedMb) else "0.0 MB/s",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                }

                // Latency / Ping
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Latency",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Ping",
                            fontSize = 10.sp,
                            color = Color(0xFFA1A1AA)
                        )
                        Text(
                            text = if (isConnected) "$pingMs ms" else "-- ms",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
