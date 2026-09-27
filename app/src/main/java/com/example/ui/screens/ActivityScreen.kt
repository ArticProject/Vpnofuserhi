package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ActivityScreen(
    sessions: List<VpnSession>,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmClear by remember { mutableStateOf(false) }

    val totalDurationSeconds = remember(sessions) { sessions.sumOf { it.durationSeconds } }
    val totalBytes = remember(sessions) { sessions.sumOf { it.bytesDownloaded + it.bytesUploaded } }

    val totalHours = totalDurationSeconds / 3600
    val totalMinutes = (totalDurationSeconds % 3600) / 60
    val totalDataMb = totalBytes / (1024f * 1024f)

    if (showConfirmClear) {
        AlertDialog(
            onDismissRequest = { showConfirmClear = false },
            title = {
                Text(
                    text = "Clear Session History",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will erase all local encrypted session records stored on this device. Cryptographic state is already zero-knowledge.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF71717A)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showConfirmClear = false
                    }
                ) {
                    Text(
                        text = "Clear All",
                        color = Color(0xFFF43F5E),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClear = false }) {
                    Text(text = "Cancel", color = Color(0xFF09090B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(22.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ENCRYPTED LOGS",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF71717A),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tunnel telemetry.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = (-1.0).sp
                )
            }

            if (sessions.isNotEmpty()) {
                IconButton(
                    onClick = { showConfirmClear = true },
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Clear History",
                        tint = Color(0xFF71717A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aggregate Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryStat(
                    title = "PROTECTED SESSIONS",
                    value = "${sessions.size}"
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(Color(0xFFE5E5EA))
                )
                SummaryStat(
                    title = "TUNNEL TIME",
                    value = "${totalHours}h ${totalMinutes}m"
                )
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                        .background(Color(0xFFE5E5EA))
                )
                SummaryStat(
                    title = "DATA TRANSFERRED",
                    value = String.format("%.1f MB", totalDataMb)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF4F4F6))
                            .border(1.dp, Color(0xFFE5E5EA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = null,
                            tint = Color(0xFF71717A),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "No Session History",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF09090B),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Completed encrypted connections will automatically log cryptographic time and traffic volume here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF71717A),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(sessions, key = { it.id }) { session ->
                    SessionItemRow(session = session)
                }
            }
        }
    }
}

@Composable
private fun SummaryStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF71717A),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF09090B),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SessionItemRow(session: VpnSession) {
    val dateStr = remember(session.startTimeMillis) {
        val sdf = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
        sdf.format(Date(session.startTimeMillis))
    }

    val durMin = session.durationSeconds / 60
    val durSec = session.durationSeconds % 60
    val totalMb = (session.bytesDownloaded + session.bytesUploaded) / (1024f * 1024f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(18.dp))
            .padding(14.dp)
            .testTag("session_item_${session.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = session.flagEmoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${session.country} · ${session.city}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF09090B),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dateStr · ${session.protocol}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF71717A),
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${durMin}m ${durSec}s",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF09090B),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = String.format("%.2f MB", totalMb),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF71717A),
                    fontSize = 10.sp
                )
            }
        }
    }
}
