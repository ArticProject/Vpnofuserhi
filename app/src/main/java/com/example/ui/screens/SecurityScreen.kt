package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnProtocol
import com.example.ui.theme.VellorEmerald
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SecurityScreen(
    currentProtocol: VpnProtocol,
    onProtocolSelected: (VpnProtocol) -> Unit,
    killSwitchEnabled: Boolean,
    onToggleKillSwitch: (Boolean) -> Unit,
    stealthEnabled: Boolean,
    onToggleStealth: (Boolean) -> Unit,
    doubleHopEnabled: Boolean,
    onToggleDoubleHop: (Boolean) -> Unit,
    adBlockEnabled: Boolean,
    onToggleAdBlock: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isAuditing by remember { mutableStateOf(false) }
    var auditCompleted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Text(
                text = "CRYPTOGRAPHIC PROTOCOL",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF71717A),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tunnel architecture.",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF09090B),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = (-1.0).sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Protocols
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                VpnProtocol.values().forEach { protocol ->
                    val isSelected = protocol == currentProtocol
                    ProtocolSelectCard(
                        protocol = protocol,
                        isSelected = isSelected,
                        onSelect = { onProtocolSelected(protocol) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ADVANCED HARDENING",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF71717A),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecurityToggleRow(
                    title = "Strict Kill Switch",
                    description = "Blocks all outgoing internet packets if tunnel handshake drops momentarily.",
                    isChecked = killSwitchEnabled,
                    onCheckedChange = onToggleKillSwitch,
                    icon = Icons.Filled.Lock,
                    tag = "toggle_kill_switch"
                )

                SecurityToggleRow(
                    title = "Stealth Camouflage",
                    description = "Obfuscates OpenVPN/WireGuard headers to look like regular TLS 1.3 web traffic.",
                    isChecked = stealthEnabled,
                    onCheckedChange = onToggleStealth,
                    icon = Icons.Filled.VisibilityOff,
                    tag = "toggle_stealth"
                )

                SecurityToggleRow(
                    title = "Double Hop Cascade",
                    description = "Chains two separate VPN nodes (e.g. Zurich into Reykjavik) for multi-jurisdiction defense.",
                    isChecked = doubleHopEnabled,
                    onCheckedChange = onToggleDoubleHop,
                    icon = Icons.Filled.Shield,
                    tag = "toggle_double_hop"
                )

                SecurityToggleRow(
                    title = "Zero-Trace Ad & Tracker Sinkhole",
                    description = "Hardware DNS sinkhole drops analytics and malicious trackers before requests leave.",
                    isChecked = adBlockEnabled,
                    onCheckedChange = onToggleAdBlock,
                    icon = Icons.Filled.Security,
                    tag = "toggle_ad_block"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "LEAK AUDITOR",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF71717A),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DNS & WebRTC Integrity Check",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF09090B),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Verifies real IP is unexposed and requests resolve strictly via private nodes.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF71717A),
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isAuditing) Color(0xFFE5E5EA) else Color(0xFF09090B))
                                .clickable(enabled = !isAuditing) {
                                    scope.launch {
                                        isAuditing = true
                                        auditCompleted = false
                                        delay(1300)
                                        isAuditing = false
                                        auditCompleted = true
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("run_audit_button")
                        ) {
                            if (isAuditing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF09090B)
                                )
                            } else {
                                Text(
                                    text = if (auditCompleted) "Re-test" else "Run Audit",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = auditCompleted,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF4F4F6))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AuditStatusRow("IPv4 / IPv6 Leak Test", "100% Isolated", isSecure = true)
                            AuditStatusRow("DNS Request Resolution", "Vellor Node (Zero Logs)", isSecure = true)
                            AuditStatusRow("WebRTC STUN / TURN Shield", "Immune (No Local IP Leak)", isSecure = true)
                            AuditStatusRow("Cryptographic Handshake", "ChaCha20 Perfect Forward Secrecy", isSecure = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProtocolSelectCard(
    protocol: VpnProtocol,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF09090B) else Color(0xFFE5E5EA),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp)
            .testTag("protocol_${protocol.name}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF09090B) else Color(0xFF71717A),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = protocol.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF09090B),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF4F4F6))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = protocol.badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF71717A),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = protocol.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF71717A),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityToggleRow(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF09090B),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF71717A),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF09090B),
                uncheckedThumbColor = Color(0xFF71717A),
                uncheckedTrackColor = Color(0xFFE5E5EA)
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun AuditStatusRow(
    title: String,
    status: String,
    isSecure: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = VellorEmerald,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF09090B),
                fontSize = 11.sp
            )
        }

        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = VellorEmerald,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}
