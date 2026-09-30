package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.AppLanguage
import com.example.model.VpnProtocol
import com.example.ui.theme.VellorEmerald

@Composable
fun SecurityScreen(
    currentProtocol: VpnProtocol,
    killSwitch: Boolean,
    stealth: Boolean,
    doubleHop: Boolean,
    adBlock: Boolean,
    onSelectProtocol: (VpnProtocol) -> Unit,
    onToggleKillSwitch: () -> Unit,
    onToggleStealth: () -> Unit,
    onToggleDoubleHop: () -> Unit,
    onToggleAdBlock: () -> Unit,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
    ) {
        item {
            Text(
                text = if (isRu) "ШИФРОВАНИЕ И БЕЗОПАСНОСТЬ" else "SECURITY & ENCRYPTION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color(0xFFA1A1AA)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isRu) "Протокол связи" else "Tunnel Protocol",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(VpnProtocol.values().size) { index ->
            val proto = VpnProtocol.values()[index]
            val isSelected = proto == currentProtocol

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
                    .border(
                        1.dp,
                        if (isSelected) VellorEmerald else Color(0xFF27272A),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectProtocol(proto) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = proto.displayName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF27272A))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = proto.badge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) VellorEmerald else Color(0xFFA1A1AA)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = proto.description,
                            fontSize = 12.sp,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isRu) "ФУНКЦИИ ЗАЩИТЫ" else "PROTECTION MODULES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color(0xFFA1A1AA)
            )

            Spacer(modifier = Modifier.height(14.dp))

            SecurityToggleItem(
                title = if (isRu) "Аварийная блокировка (Kill Switch)" else "Kill Switch",
                desc = if (isRu) "Блокирует интернет при случайном разрыве туннеля" else "Block internet if VPN disconnects",
                checked = killSwitch,
                onCheckedChange = { onToggleKillSwitch() },
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecurityToggleItem(
                title = if (isRu) "Маскировка трафика (Stealth)" else "Stealth Camouflage",
                desc = if (isRu) "Имитирует обычный HTTPS трафик против DPI фильтров" else "Camouflage VPN packets as web traffic",
                checked = stealth,
                onCheckedChange = { onToggleStealth() },
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecurityToggleItem(
                title = if (isRu) "Двойной прыжок (Double Hop)" else "Double Hop Routing",
                desc = if (isRu) "Шифрование через две независимые ноды подряд" else "Route traffic through two chained servers",
                checked = doubleHop,
                onCheckedChange = { onToggleDoubleHop() },
                isDarkTheme = isDarkTheme
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecurityToggleItem(
                title = if (isRu) "Блокировщик рекламы и трекеров" else "Ad & Tracker Shield",
                desc = if (isRu) "DNS-фильтрация вредоносных баннеров и слежки" else "DNS filtering of telemetry trackers",
                checked = adBlock,
                onCheckedChange = { onToggleAdBlock() },
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun SecurityToggleItem(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDarkTheme: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = Color(0xFFA1A1AA)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = VellorEmerald
                )
            )
        }
    }
}
