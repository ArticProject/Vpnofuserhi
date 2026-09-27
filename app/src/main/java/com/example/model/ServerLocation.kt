package com.example.model

data class ServerLocation(
    val id: String,
    val country: String,
    val city: String,
    val countryCode: String,
    val cityCode: String,
    val flagEmoji: String = "",
    val photoUrl: String = "",
    val pingMs: Int,
    val loadPercent: Int,
    val ipAddress: String,
    val isP2p: Boolean = false,
    val isStreaming: Boolean = false,
    val isDoubleVpn: Boolean = false,
    val isStealth: Boolean = false,
    val isFavorite: Boolean = false
) {
    val fullName: String get() = "$city, $countryCode"
}
