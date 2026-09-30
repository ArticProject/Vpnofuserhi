package com.example.ui.components

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnProtocol
import com.example.model.VpnState
import com.example.ui.theme.VellorEmerald

@Composable
fun GlassmorphismSpeedProtocolPanel(
    vpnState: VpnState,
    protocol: VpnProtocol,
    downloadSpeedMb: Float,
    uploadSpeedMb: Float,
    durationFormatted: String,
    pingMs: Int,
    onProtocolClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = vpnState == VpnState.CONNECTED

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpeedMetricGlassCard(
                icon = Icons.Filled.ArrowDownward,
                label = "DOWN",
                value = if (isConnected) String.format("%.1f", downloadSpeedMb) else "0.0",
                unit = "MB/S",
                accentColor = if (isConnected) VellorEmerald else Color(0xFF71717A),
                modifier = Modifier.weight(1f)
            )

            SpeedMetricGlassCard(
                icon = Icons.Filled.ArrowUpward,
                label = "UP",
                value = if (isConnected) String.format("%.1f", uploadSpeedMb) else "0.0",
                unit = "MB/S",
                accentColor = Color(0xFF71717A),
                modifier = Modifier.weight(1f)
            )

            SpeedMetricGlassCard(
                icon = Icons.Filled.Timer,
                label = "UPTIME",
                value = if (isConnected) durationFormatted else "00:00",
                unit = "ACTIVE",
                accentColor = Color(0xFF71717A),
                modifier = Modifier.weight(1f)
            )

            SpeedMetricGlassCard(
                icon = Icons.Filled.Speed,
                label = "LATENCY",
                value = if (isConnected) "$pingMs" else "--",
                unit = "MS",
                accentColor = if (isConnected && pingMs < 30) VellorEmerald else Color(0xFF71717A),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF4F4F6))
                .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
                .bounceClick(scaleDown = 0.96f, onClick = onProtocolClick)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TUNNEL CIPHER",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF71717A),
                        fontSize = 10.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${protocol.displayName} · ${protocol.cipher}",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF09090B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF09090B))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = protocol.badge,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SpeedMetricGlassCard(
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF4F4F6))
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF71717A)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF09090B)
            )
            Text(
                text = unit,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA1A1AA)
            )
        }
    }
}
