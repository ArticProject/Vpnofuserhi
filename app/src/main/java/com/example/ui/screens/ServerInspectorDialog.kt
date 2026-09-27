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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerLocation
import com.example.model.VpnProtocol
import com.example.ui.theme.VellorEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerInspectorDialog(
    server: ServerLocation,
    protocol: VpnProtocol,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE5E5EA))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = server.flagEmoji,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = server.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF09090B),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "NODE INSPECTION & SPECS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF71717A),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF09090B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Specs Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4F4F6))
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                InspectorRow(label = "IP ADDRESS", value = server.ipAddress, isMono = true)
                InspectorRow(label = "PING / LATENCY", value = "${server.pingMs} ms (Jitter: ±0.8ms)", isHighlight = true)
                InspectorRow(label = "CURRENT LOAD", value = "${server.loadPercent}% capacity (Optimal)")
                InspectorRow(label = "PROTOCOL", value = protocol.displayName)
                InspectorRow(label = "CIPHER SUITE", value = protocol.cipher, isMono = true)
                InspectorRow(label = "DOUBLE HOP SUPPORT", value = if (server.isDoubleVpn) "Enabled (Tier 1)" else "Standard Hop")
                InspectorRow(label = "P2P BITTORENT", value = if (server.isP2p) "Permitted (Zero Throttle)" else "Blocked")
                InspectorRow(label = "STEALTH OBFUSCATION", value = if (server.isStealth) "TLS 1.3 Camouflage" else "Standard")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RAM-Only Attestation Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF4F4F6))
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = VellorEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "NO-LOGS ATTESTATION VERIFIED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF09090B),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "RAM-only diskless node. Ephemeral cryptographic keys wiped on handshake renewal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF71717A),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InspectorRow(
    label: String,
    value: String,
    isMono: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF71717A),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isHighlight) VellorEmerald else Color(0xFF09090B),
            fontWeight = FontWeight.SemiBold,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.SansSerif,
            fontSize = 12.sp
        )
    }
}
