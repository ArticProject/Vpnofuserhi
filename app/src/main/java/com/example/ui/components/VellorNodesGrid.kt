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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.model.ServerLocation
import com.example.ui.theme.VellorAmber
import com.example.ui.theme.VellorEmerald

@Composable
fun VellorFeaturedNodesSection(
    servers: List<ServerLocation>,
    selectedServer: ServerLocation,
    onSelectServer: (ServerLocation) -> Unit,
    onSeeAll: () -> Unit,
    isDarkTheme: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.SYSTEM,
    modifier: Modifier = Modifier
) {
    val isRu = currentLanguage == AppLanguage.RUSSIAN

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isRu) "ИЗБРАННЫЕ НОДЫ" else "FEATURED NODES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color(0xFFA1A1AA)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAll() }
            ) {
                Text(
                    text = if (isRu) "Все ноды" else "See all",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "See all",
                    tint = if (isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        servers.take(4).forEach { server ->
            FeaturedNodeCard(
                server = server,
                isSelected = server.id == selectedServer.id,
                onSelect = { onSelectServer(server) },
                isDarkTheme = isDarkTheme
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun FeaturedNodeCard(
    server: ServerLocation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    isDarkTheme: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkTheme) Color(0xFF141416) else Color(0xFFF4F4F5))
            .border(
                width = 1.dp,
                color = if (isSelected) VellorEmerald
                else if (isDarkTheme) Color(0xFF27272A)
                else Color(0xFFE4E4E7),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = server.flagEmoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = server.city,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
                Text(
                    text = "${server.country} • ${server.ipAddress}",
                    fontSize = 11.sp,
                    color = Color(0xFFA1A1AA)
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF27272A))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${server.pingMs}ms",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (server.pingMs < 30) VellorEmerald else Color.White
                )
            }
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(VellorEmerald)
                )
            }
        }
    }
}
