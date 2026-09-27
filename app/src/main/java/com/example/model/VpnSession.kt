package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_sessions")
data class VpnSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val serverName: String,
    val city: String,
    val country: String,
    val flagEmoji: String,
    val startTimeMillis: Long,
    val durationSeconds: Long,
    val bytesDownloaded: Long,
    val bytesUploaded: Long,
    val protocol: String
)
