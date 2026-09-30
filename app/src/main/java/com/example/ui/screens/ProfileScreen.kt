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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.VpnSession
import com.example.ui.theme.VellorEmerald
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    username: String,
    userEmail: String,
    sovereignId: String,
    isRegistered: Boolean,
    isActivated: Boolean,
    activatedKey: String,
    activationError: String?,
    sessions: List<VpnSession>,
    onActivateKey: (String) -> Unit,
    onDeactivateKey: () -> Unit,
    onRegisterUser: (String, String) -> Unit,
    onClearHistory: () -> Unit,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN
    var inputKey by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
    ) {
        // User Identity Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) Color.White else Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = if (isDarkTheme) Color.Black else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = username,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "VIP",
                                tint = VellorEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = sovereignId,
                            fontSize = 11.sp,
                            color = Color(0xFFA1A1AA)
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF27272A))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isActivated) "LIFETIME VIP ACTIVE" else "FREE TIER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActivated) VellorEmerald else Color(0xFFA1A1AA)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Activation Key Input
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Key",
                            tint = VellorEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRu) "Ключ доступа / Подписка" else "Access Key Activation",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color.White else Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isRu) "Активный ключ: $activatedKey"
                        else "Active Key: $activatedKey",
                        fontSize = 12.sp,
                        color = Color(0xFFA1A1AA)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { inputKey = it },
                        placeholder = { Text(if (isRu) "Введите ключ (напр. H1CLOUD-2026)" else "Enter key (e.g. H1CLOUD-2026)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (inputKey.isNotBlank()) {
                                onActivateKey(inputKey)
                                inputKey = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDarkTheme) Color.White else Color.Black)
                    ) {
                        Text(
                            text = if (isRu) "Активировать ключ" else "Activate Key",
                            color = if (isDarkTheme) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Session History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = Color(0xFFA1A1AA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRu) "ИСТОРИЯ СЕССИЙ" else "SESSION HISTORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFFA1A1AA)
                    )
                }

                if (sessions.isNotEmpty()) {
                    Text(
                        text = if (isRu) "Очистить" else "Clear",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.clickable { onClearHistory() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (sessions.isEmpty()) {
            item {
                Text(
                    text = if (isRu) "Нет сохранённых сессий подключения" else "No recorded sessions yet",
                    fontSize = 12.sp,
                    color = Color(0xFFA1A1AA),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
                        .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${session.flagEmoji} ${session.city}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                            val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(session.startTimeMillis))
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = Color(0xFFA1A1AA)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${session.durationSeconds}s",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = VellorEmerald
                            )
                            Text(
                                text = session.protocol,
                                fontSize = 10.sp,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}
