package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ActivationRequiredDialog(
    onDismissRequest: () -> Unit,
    onActivateKey: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    activationError: String?,
    isDarkTheme: Boolean = true
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(if (isDarkTheme) Color(0xFF141416) else Color.White)
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "LIFETIME PASS REQUIRED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFFA1A1AA)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Активация узлов Vellor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Для доступа ко всей сети премиальных серверов с пропускной способностью 10 Gbps активируйте ключ доступа.",
                    fontSize = 12.sp,
                    color = Color(0xFFA1A1AA)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onNavigateToProfile,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDarkTheme) Color.White else Color.Black)
                ) {
                    Text(
                        text = "Ввести ключ в профиле",
                        color = if (isDarkTheme) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
