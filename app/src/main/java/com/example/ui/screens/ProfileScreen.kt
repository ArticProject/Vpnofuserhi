package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.VpnSession
import com.example.ui.components.bounceClick
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceInner
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
    onActivateKey: (String) -> Boolean,
    onDeactivateKey: () -> Unit,
    onRegisterUser: (String, String) -> Unit,
    onClearHistory: () -> Unit,
    isDarkTheme: Boolean = false,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    var keyInputText by remember { mutableStateOf("") }
    var showRegisterDialog by remember { mutableStateOf(false) }

    val totalSeconds = sessions.sumOf { it.durationSeconds }
    val totalHours = totalSeconds / 3600f
    val totalBytes = sessions.sumOf { it.bytesDownloaded + it.bytesUploaded }
    val totalGb = totalBytes / (1024f * 1024f * 1024f)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Title
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = if (isRu) "Профиль оператора" else "Sovereign Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = if (isRu) "Управление цифровым суверенитетом и лицензией"
                    else "Digital sovereign identity & cryptographic access pass",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
                )
            }
        }

        // 2. User Identity Card (Avatar, Name, ID, Registration status)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
                    .border(
                        1.dp,
                        if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Monogram Avatar
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = if (isDarkTheme) listOf(Color(0xFF27272A), Color(0xFF18181B))
                                        else listOf(Color(0xFFE5E5EA), Color(0xFFD4D4D8))
                                    )
                                )
                                .border(
                                    1.2.dp,
                                    if (isActivated) VellorEmerald else (if (isDarkTheme) DarkSurfaceBorder else Color(0xFFD4D4D8)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = username.take(2).uppercase().ifBlank { "SO" },
                                color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = username,
                                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isRegistered) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Verified Member",
                                        tint = VellorEmerald,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            // Sovereign ID badge (clickable to copy)
                            Row(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                                    .bounceClick(scaleDown = 0.95f) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Sovereign ID", sovereignId))
                                        Toast.makeText(context, if (isRu) "ID скопирован в буфер" else "ID copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = sovereignId,
                                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy ID",
                                    tint = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Edit profile button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                            .border(1.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), CircleShape)
                            .bounceClick(scaleDown = 0.90f) { showRegisterDialog = true }
                            .padding(9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Profile",
                            tint = if (isDarkTheme) Color.White else Color(0xFF09090B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 3. KEY ACTIVATION & SUBSCRIPTION HERO CARD
        item {
            if (isActivated) {
                // Activated Card: Lifetime Full Access
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDarkTheme) Color(0xFF0D1612) else Color(0xFFF0FDF4))
                        .border(1.2.dp, VellorEmerald.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(VellorEmerald.copy(alpha = 0.15f))
                                    .border(1.dp, VellorEmerald.copy(alpha = 0.4f), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(VellorEmerald)
                                    )
                                    Text(
                                        text = if (isRu) "АКТИВЕН · БЕЗЛИМИТ" else "LIFETIME ACCESS · ACTIVE",
                                        color = VellorEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Filled.LockOpen,
                                contentDescription = null,
                                tint = VellorEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isRu) "Полный суверенный доступ" else "Full Sovereign Lifetime Pass",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isDarkTheme) Color.White else Color(0xFF065F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isRu) "Все серверы 10 Gbps разблокированы без ограничений"
                                else "All high-speed 10 Gbps nodes unmetered and unlocked",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF047857),
                                fontSize = 12.sp
                            )
                        }

                        // Subscription Details Grid
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDarkTheme) Color(0xFF080C0A) else Color.White.copy(alpha = 0.8f))
                                .border(0.8.dp, if (isDarkTheme) Color(0xFF1E2E25) else Color(0xFFBBF7D0), RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ProfileDetailRow(
                                label = if (isRu) "Срок действия" else "Validity Period",
                                value = if (isRu) "Навсегда (Бессрочно)" else "Lifetime (No Expiry)",
                                valueColor = VellorEmerald,
                                isDarkTheme = isDarkTheme
                            )
                            ProfileDetailRow(
                                label = if (isRu) "Ключ лицензии" else "Active License Key",
                                value = activatedKey.ifBlank { "SOVEREIGN-KEY" },
                                isMonospace = true,
                                isDarkTheme = isDarkTheme
                            )
                            ProfileDetailRow(
                                label = if (isRu) "Скорость туннеля" else "Tunnel Bandwidth",
                                value = "10 Gbps WireGuard Dedicated",
                                isDarkTheme = isDarkTheme
                            )
                            ProfileDetailRow(
                                label = if (isRu) "Лимит устройств" else "Connected Devices",
                                value = if (isRu) "До 5 устройств" else "Up to 5 devices",
                                isDarkTheme = isDarkTheme
                            )
                            ProfileDetailRow(
                                label = if (isRu) "Активный узел" else "Active Node",
                                value = "🇩🇪 Germany (de1.h1cloud.net)",
                                isDarkTheme = isDarkTheme
                            )
                        }

                        // Copy VLESS Key button
                        val vlessKey = "vless://e1b667c1-2438-471a-b1cd-9ba90d557e9d@de1.h1cloud.net:25558?security=reality&encryption=none&pbk=bls7cc5bJGz20-1A_DFdRvs5HT3hs1nAWRqjpk036RY&headerType=none&fp=android&type=tcp&sni=proxy11.h1guro.ovh&sid=f16035de5d48395f#Vellor%20Germany%201"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(VellorEmerald.copy(alpha = 0.15f))
                                .border(1.dp, VellorEmerald.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .bounceClick(scaleDown = 0.96f) {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("VLESS Key", vlessKey))
                                    Toast.makeText(context, if (isRu) "VLESS ключ скопирован в буфер!" else "VLESS key copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy VLESS Key",
                                    tint = VellorEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isRu) "Скопировать боевой VLESS Reality ключ" else "Copy Active VLESS Reality Key",
                                    color = VellorEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Revoke / Change Key
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = if (isRu) "Отозвать или сменить ключ" else "Revoke or change key",
                                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF64748B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .bounceClick(scaleDown = 0.96f) { onDeactivateKey() }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                // Inactive Card: Key Input Required
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
                        .border(1.2.dp, if (isDarkTheme) Color(0xFF3F3F46) else Color(0xFFE5E5EA), RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444).copy(alpha = 0.12f))
                                    .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f), CircleShape)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEF4444))
                                    )
                                    Text(
                                        text = if (isRu) "ТРЕБУЕТСЯ АКТИВАЦИЯ" else "KEY ACTIVATION REQUIRED",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isRu) "Активируйте ключ доступа" else "Enter Sovereign Access Key",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (isRu)
                                    "Для запуска WireGuard туннелей введите персональный код подписки."
                                else
                                    "Enter your cryptographic subscription key to unlock sovereign routing nodes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                                fontSize = 13.sp
                            )
                        }

                        // Key Input Field
                        OutlinedTextField(
                            value = keyInputText,
                            onValueChange = { keyInputText = it.uppercase() },
                            placeholder = {
                                Text(
                                    text = if (isRu) "Например: VELLOR-VIP" else "e.g. VELLOR-VIP",
                                    color = if (isDarkTheme) Color(0xFF52525B) else Color(0xFFA1A1AA),
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.VpnKey,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            isError = activationError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDarkTheme) Color.White else Color(0xFF09090B),
                                unfocusedBorderColor = if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                                focusedContainerColor = if (isDarkTheme) DarkSurfaceInner else Color(0xFFF9F9FB),
                                unfocusedContainerColor = if (isDarkTheme) DarkSurfaceInner else Color(0xFFF9F9FB),
                                errorBorderColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    if (keyInputText.isNotBlank()) {
                                        onActivateKey(keyInputText)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("access_key_input")
                        )

                        // Error message display
                        if (activationError != null) {
                            Text(
                                text = activationError,
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Activate Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(if (isDarkTheme) Color.White else Color(0xFF09090B))
                                .bounceClick(scaleDown = 0.95f) {
                                    keyboardController?.hide()
                                    if (keyInputText.isNotBlank()) {
                                        onActivateKey(keyInputText)
                                    }
                                }
                                .testTag("activate_key_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Key,
                                    contentDescription = null,
                                    tint = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isRu) "Активировать доступ" else "Activate Sovereign Pass",
                                    color = if (isDarkTheme) Color(0xFF09090B) else Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Clickable Test Key Pills
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (isRu) "Быстрые ключи для активации (нажмите для вставки):"
                                else "Available trial keys (tap to fill):",
                                fontSize = 11.sp,
                                color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("H1CLOUD-2026", "VELLOR-VIP", "SOVEREIGN-2026", "LIFETIME-ACCESS").forEach { testKey ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
                                            .border(0.8.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                                            .bounceClick(scaleDown = 0.94f) {
                                                keyInputText = testKey
                                                onActivateKey(testKey)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = testKey,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkTheme) Color.White else Color(0xFF09090B)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Lifetime Usage Telemetry Summary
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
                    .border(
                        1.dp,
                        if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA),
                        RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = if (isRu) "Статистика туннеля" else "Tunnel Telemetry",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileMetricBlock(
                            label = if (isRu) "Трафик" else "Data Saved",
                            value = String.format("%.2f GB", totalGb),
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBlock(
                            label = if (isRu) "Время защиты" else "Protected Time",
                            value = String.format("%.1f ч", totalHours),
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileMetricBlock(
                            label = if (isRu) "Сессий" else "Sessions",
                            value = "${sessions.size}",
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. Recent Sessions Log (Integrated from previous Logs tab)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRu) "Журнал подключений" else "Session History",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                if (sessions.isNotEmpty()) {
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = "Clear History",
                            tint = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (sessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
                        .border(1.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isRu) "История подключений пока пуста" else "No encrypted sessions recorded yet",
                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A),
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(sessions.take(10)) { session ->
                SessionHistoryItemCard(
                    session = session,
                    isDarkTheme = isDarkTheme,
                    isRu = isRu
                )
            }
        }
    }

    // Registration / Edit Profile Dialog
    if (showRegisterDialog) {
        var tempName by remember { mutableStateOf(username) }
        var tempEmail by remember { mutableStateOf(userEmail) }

        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Text(
                    text = if (isRu) "Профиль пользователя" else "Operator Profile",
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isRu) "Укажите имя для идентификации в суверенной сети:"
                        else "Enter your moniker for sovereign network routing:",
                        fontSize = 13.sp,
                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
                    )
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(if (isRu) "Имя оператора" else "Moniker") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text(if (isRu) "Email (необязательно)" else "Email (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRegisterUser(tempName, tempEmail)
                        showRegisterDialog = false
                    }
                ) {
                    Text(
                        text = if (isRu) "Сохранить" else "Save",
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF09090B)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text(
                        text = if (isRu) "Отмена" else "Cancel",
                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
                    )
                }
            },
            containerColor = if (isDarkTheme) Color(0xFF18181B) else Color.White
        )
    }
}

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String,
    valueColor: Color? = null,
    isMonospace: Boolean = false,
    isDarkTheme: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
            color = valueColor ?: (if (isDarkTheme) Color.White else Color(0xFF09090B))
        )
    }
}

@Composable
private fun ProfileMetricBlock(
    label: String,
    value: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isDarkTheme) DarkSurfaceInner else Color(0xFFF4F4F6))
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDarkTheme) Color.White else Color(0xFF09090B)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
        )
    }
}

@Composable
private fun SessionHistoryItemCard(
    session: VpnSession,
    isDarkTheme: Boolean,
    isRu: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    val timeFormatted = remember(session.startTimeMillis) {
        dateFormat.format(Date(session.startTimeMillis))
    }
    val mbTotal = (session.bytesDownloaded + session.bytesUploaded) / (1024f * 1024f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) DarkSurfaceCard else Color.White)
            .border(1.dp, if (isDarkTheme) DarkSurfaceBorder else Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = session.flagEmoji.ifBlank { "🌐" },
                    fontSize = 20.sp
                )
                Column {
                    Text(
                        text = "${session.country} · ${session.city}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color(0xFF09090B)
                    )
                    Text(
                        text = "$timeFormatted · ${session.protocol}",
                        fontSize = 11.sp,
                        color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f MB", mbTotal),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color(0xFF09090B)
                )
                Text(
                    text = "${session.durationSeconds / 60}m ${session.durationSeconds % 60}s",
                    fontSize = 11.sp,
                    color = if (isDarkTheme) Color(0xFFA1A1AA) else Color(0xFF71717A)
                )
            }
        }
    }
}
