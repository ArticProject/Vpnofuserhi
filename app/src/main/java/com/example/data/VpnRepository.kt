package com.example.data

import com.example.model.ServerLocation
import com.example.model.VpnSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VpnRepository(private val sessionDao: VpnSessionDao) {

    private val initialServers = listOf(
        ServerLocation(
            id = "ch_zrh_01",
            country = "Switzerland",
            city = "Zurich",
            countryCode = "CH",
            flagEmoji = "🇨🇭",
            pingMs = 12,
            loadPercent = 28,
            ipAddress = "185.220.101.42",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = true,
            isStealth = true,
            isFavorite = true
        ),
        ServerLocation(
            id = "is_rkv_01",
            country = "Iceland",
            city = "Reykjavik",
            countryCode = "IS",
            flagEmoji = "🇮🇸",
            pingMs = 24,
            loadPercent = 19,
            ipAddress = "193.182.144.12",
            isP2p = true,
            isStreaming = false,
            isDoubleVpn = true,
            isStealth = true,
            isFavorite = true
        ),
        ServerLocation(
            id = "jp_tyo_01",
            country = "Japan",
            city = "Tokyo",
            countryCode = "JP",
            flagEmoji = "🇯🇵",
            pingMs = 45,
            loadPercent = 42,
            ipAddress = "133.242.18.99",
            isP2p = false,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = true,
            isFavorite = false
        ),
        ServerLocation(
            id = "nl_ams_01",
            country = "Netherlands",
            city = "Amsterdam",
            countryCode = "NL",
            flagEmoji = "🇳🇱",
            pingMs = 18,
            loadPercent = 35,
            ipAddress = "94.142.241.11",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = false,
            isFavorite = false
        ),
        ServerLocation(
            id = "de_fra_01",
            country = "Germany",
            city = "Frankfurt",
            countryCode = "DE",
            flagEmoji = "🇩🇪",
            pingMs = 16,
            loadPercent = 31,
            ipAddress = "159.69.198.85",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = true,
            isFavorite = false
        ),
        ServerLocation(
            id = "gb_lon_01",
            country = "United Kingdom",
            city = "London",
            countryCode = "GB",
            flagEmoji = "🇬🇧",
            pingMs = 21,
            loadPercent = 44,
            ipAddress = "178.62.24.103",
            isP2p = false,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = false,
            isFavorite = false
        ),
        ServerLocation(
            id = "se_sto_01",
            country = "Sweden",
            city = "Stockholm",
            countryCode = "SE",
            flagEmoji = "🇸🇪",
            pingMs = 26,
            loadPercent = 22,
            ipAddress = "185.157.162.5",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = true,
            isStealth = false,
            isFavorite = false
        ),
        ServerLocation(
            id = "sg_sin_01",
            country = "Singapore",
            city = "Singapore",
            countryCode = "SG",
            flagEmoji = "🇸🇬",
            pingMs = 58,
            loadPercent = 38,
            ipAddress = "128.199.204.60",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = true,
            isFavorite = false
        ),
        ServerLocation(
            id = "us_nyc_01",
            country = "United States",
            city = "New York",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            pingMs = 74,
            loadPercent = 52,
            ipAddress = "198.199.112.44",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = false,
            isFavorite = false
        ),
        ServerLocation(
            id = "ca_tor_01",
            country = "Canada",
            city = "Toronto",
            countryCode = "CA",
            flagEmoji = "🇨🇦",
            pingMs = 69,
            loadPercent = 27,
            ipAddress = "159.203.41.88",
            isP2p = true,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = false,
            isFavorite = false
        ),
        ServerLocation(
            id = "au_syd_01",
            country = "Australia",
            city = "Sydney",
            countryCode = "AU",
            flagEmoji = "🇦🇺",
            pingMs = 120,
            loadPercent = 33,
            ipAddress = "139.180.170.21",
            isP2p = false,
            isStreaming = true,
            isDoubleVpn = false,
            isStealth = false,
            isFavorite = false
        )
    )

    private val _servers = MutableStateFlow(initialServers)
    val servers = _servers.asStateFlow()

    val allSessions: Flow<List<VpnSession>> = sessionDao.getAllSessions()

    fun toggleFavorite(serverId: String) {
        _servers.update { list ->
            list.map { if (it.id == serverId) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    suspend fun saveSession(session: VpnSession): Long = sessionDao.insertSession(session)

    suspend fun clearHistory() = sessionDao.clearAll()

    suspend fun deleteSession(id: Long) = sessionDao.deleteById(id)
}
