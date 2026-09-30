package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppTab

@Composable
fun VellorSafariBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(if (isDarkTheme) Color(0xFF141416) else Color.White)
                .border(
                    width = 1.dp,
                    color = if (isDarkTheme) Color(0xFF27272A) else Color(0xFFE4E4E7),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SafariTabItem(
                title = if (isRu) "Туннель" else "Tunnel",
                icon = Icons.Default.VpnKey,
                isSelected = currentTab == AppTab.TUNNEL,
                isDarkTheme = isDarkTheme,
                onClick = { onTabSelected(AppTab.TUNNEL) }
            )
            SafariTabItem(
                title = if (isRu) "Ноды" else "Nodes",
                icon = Icons.Default.Dns,
                isSelected = currentTab == AppTab.NODES,
                isDarkTheme = isDarkTheme,
                onClick = { onTabSelected(AppTab.NODES) }
            )
            SafariTabItem(
                title = if (isRu) "Защита" else "Shield",
                icon = Icons.Default.Security,
                isSelected = currentTab == AppTab.SHIELD,
                isDarkTheme = isDarkTheme,
                onClick = { onTabSelected(AppTab.SHIELD) }
            )
            SafariTabItem(
                title = if (isRu) "Профиль" else "Profile",
                icon = Icons.Default.Person,
                isSelected = currentTab == AppTab.PROFILE,
                isDarkTheme = isDarkTheme,
                onClick = { onTabSelected(AppTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun SafariTabItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) {
        if (isDarkTheme) Color.White else Color.Black
    } else {
        Color.Transparent
    }

    val contentColor = if (isSelected) {
        if (isDarkTheme) Color.Black else Color.White
    } else {
        Color(0xFFA1A1AA)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.height(18.dp)
            )
            if (isSelected) {
                Text(
                    text = title,
                    color = contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
