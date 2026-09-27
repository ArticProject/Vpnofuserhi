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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerLocation
import com.example.ui.theme.VellorEmerald

@Composable
fun LocationsScreen(
    servers: List<ServerLocation>,
    selectedServer: ServerLocation,
    onSelectServer: (ServerLocation) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onInspectServer: (ServerLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterOptions = listOf("All", "Favorites", "Fastest", "Double VPN", "P2P")

    val filteredServers = remember(servers, searchQuery, selectedFilter) {
        servers.filter { server ->
            val matchesQuery = searchQuery.isBlank() ||
                server.country.contains(searchQuery, ignoreCase = true) ||
                server.city.contains(searchQuery, ignoreCase = true) ||
                server.ipAddress.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Favorites" -> server.isFavorite
                "Fastest" -> server.pingMs < 30
                "Double VPN" -> server.isDoubleVpn
                "P2P" -> server.isP2p
                else -> true
            }

            matchesQuery && matchesFilter
        }.sortedWith(
            compareByDescending<ServerLocation> { it.id == selectedServer.id }
                .thenByDescending { it.isFavorite }
                .thenBy { it.pingMs }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Section Header
        Text(
            text = "GLOBAL CATALOG",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF71717A),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.4.sp,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "All endpoints.",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF09090B),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            letterSpacing = (-1.0).sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF4F4F6))
                .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search city, country or IP...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF71717A)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF71717A),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF71717A),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF09090B),
                    unfocusedTextColor = Color(0xFF09090B),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_servers_input")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Pills
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filterOptions) { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF09090B) else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) Color(0xFF09090B) else Color(0xFFE5E5EA),
                            shape = CircleShape
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) Color.White else Color(0xFF09090B),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Server List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(filteredServers, key = { it.id }) { server ->
                val isSelected = server.id == selectedServer.id

                ServerItemRow(
                    server = server,
                    isSelected = isSelected,
                    onSelect = { onSelectServer(server) },
                    onToggleFavorite = { onToggleFavorite(server.id) },
                    onInspect = { onInspectServer(server) }
                )
            }
        }
    }
}

@Composable
private fun ServerItemRow(
    server: ServerLocation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit,
    onInspect: () -> Unit
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
            .testTag("server_item_${server.id}")
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
                Text(
                    text = server.flagEmoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = server.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF09090B),
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Active",
                                tint = VellorEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${server.ipAddress} · Load ${server.loadPercent}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF71717A),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF4F4F6))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${server.pingMs}ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (server.pingMs < 30) VellorEmerald else Color(0xFF71717A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(0.8.dp, Color(0xFFE5E5EA), CircleShape)
                        .clickable(onClick = onInspect)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Inspect",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF09090B),
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (server.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Favorite",
                        tint = if (server.isFavorite) Color(0xFF09090B) else Color(0xFF71717A),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
