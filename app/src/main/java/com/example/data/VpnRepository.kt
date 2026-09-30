package com.example.data

import com.example.model.ServerLocation
import com.example.model.VpnSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VpnRepository(private val dao: VpnSessionDao) {

    private val _servers = MutableStateFlow(
        listOf(
            ServerLocation(
                id = "srv_h1_de",
                country = "Germany",
                countryCode = "DE",
                city = "Frankfurt",
                cityCode = "FRA",
                flagEmoji = "🇩🇪",
                pingMs = 24,
                loadPercent = 12,
                ipAddress = "179.254.127.97",
                vlessUrl = "vless://e1b667c1-2438-471a-b1cd-9ba90d557e9d@de1.h1cloud.net:25562?type=tcp&security=reality&sni=www.samsung.com&fp=chrome&pbk=IaWM7egEriDsIBixWjUN1i5FWBpOhVfVRBa2edgR9HI&sid=3758385544d9dc53&spx=%2F&encryption=none#Vellor%20DE%20-%20H1Cloud",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = true
            ),
            ServerLocation(
                id = "srv_es",
                country = "Spain",
                countryCode = "ES",
                city = "Madrid",
                cityCode = "MAD",
                flagEmoji = "🇪🇸",
                pingMs = 28,
                loadPercent = 19,
                ipAddress = "185.196.220.14",
                vlessUrl = "vless://e1b667c1-2438-471a-b1cd-9ba90d557e9d@es1.h1cloud.net:25558?security=reality&encryption=none&pbk=bls7cc5bJGz20-1A_DFdRvs5HT3hs1nAWRqjpk036RY&headerType=none&fp=android&type=tcp&sni=proxy11.h1guro.ovh&sid=f16035de5d48395f#Vellor%20ES%20-%20Madrid",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = true
            ),
            ServerLocation(
                id = "srv_pl",
                country = "Poland",
                countryCode = "PL",
                city = "Warsaw",
                cityCode = "WAW",
                flagEmoji = "🇵🇱",
                pingMs = 21,
                loadPercent = 16,
                ipAddress = "193.34.212.8",
                vlessUrl = "vless://e1b667c1-2438-471a-b1cd-9ba90d557e9d@pl1.h1cloud.net:25558?security=reality&encryption=none&pbk=bls7cc5bJGz20-1A_DFdRvs5HT3hs1nAWRqjpk036RY&headerType=none&fp=android&type=tcp&sni=proxy11.h1guro.ovh&sid=f16035de5d48395f#Vellor%20PL%20-%20Warsaw",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = true
            ),
            ServerLocation(
                id = "srv_jp",
                country = "Japan",
                countryCode = "JP",
                city = "Tokyo",
                cityCode = "TYO",
                flagEmoji = "🇯🇵",
                pingMs = 38,
                loadPercent = 23,
                ipAddress = "156.146.56.88",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = true
            ),
            ServerLocation(
                id = "srv_ch",
                country = "Switzerland",
                countryCode = "CH",
                city = "Zurich",
                cityCode = "ZRH",
                flagEmoji = "🇨🇭",
                pingMs = 18,
                loadPercent = 14,
                ipAddress = "194.230.144.1",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = true
            ),
            ServerLocation(
                id = "srv_gb",
                country = "United Kingdom",
                countryCode = "GB",
                city = "London",
                cityCode = "LON",
                flagEmoji = "🇬🇧",
                pingMs = 26,
                loadPercent = 29,
                ipAddress = "185.107.56.2",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = false
            ),
            ServerLocation(
                id = "srv_nl",
                country = "Netherlands",
                countryCode = "NL",
                city = "Amsterdam",
                cityCode = "AMS",
                flagEmoji = "🇳🇱",
                pingMs = 22,
                loadPercent = 17,
                ipAddress = "149.202.88.9",
                isLiveServer = true,
                isP2p = true,
                isStreaming = true,
                isStealth = true,
                isFavorite = false
            )
        )
    )
    val servers = _servers.asStateFlow()

    fun toggleFavorite(serverId: String) {
        _servers.value = _servers.value.map { server ->
            if (server.id == serverId) server.copy(isFavorite = !server.isFavorite) else server
        }
    }

    val sessions: Flow<List<VpnSession>> = dao.getAllSessions()

    suspend fun saveSession(session: VpnSession): Long = dao.insertSession(session)

    suspend fun clearHistory() = dao.clearAll()
}
