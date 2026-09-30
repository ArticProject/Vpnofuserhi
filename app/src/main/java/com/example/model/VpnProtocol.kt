package com.example.model

enum class VpnProtocol(val displayName: String, val badge: String, val description: String) {
    VLESS_REALITY("VLESS Reality", "XTLS", "Undetectable TLS camouflage, zero latency handshake"),
    WIREGUARD("WireGuard®", "UDP", "Next-gen cryptokey routing with instant failover"),
    HYSTERIA2("Hysteria 2", "QUIC", "Aggressive UDP throughput on throttled connections"),
    SHADOWSOCKS("Shadowsocks-2022", "AEAD", "Battle-tested obfuscation against deep packet filters")
}
