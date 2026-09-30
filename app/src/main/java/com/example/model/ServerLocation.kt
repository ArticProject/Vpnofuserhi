package com.example.model

data class ServerLocation(
    val id: String,
    val country: String,
    val countryCode: String,
    val city: String,
    val cityCode: String,
    val flagEmoji: String,
    val pingMs: Int,
    val loadPercent: Int,
    val ipAddress: String,
    val vlessUrl: String = "",
    val photoUrl: String = "",
    val isLiveServer: Boolean = true,
    val isP2p: Boolean = true,
    val isStreaming: Boolean = true,
    val isStealth: Boolean = true,
    val isFavorite: Boolean = false
) {
    val fullName: String
        get() = "$country ($city)"
}
