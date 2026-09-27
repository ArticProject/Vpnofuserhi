package com.example.model

enum class VpnProtocol(
    val displayName: String,
    val cipher: String,
    val description: String,
    val badge: String
) {
    WIREGUARD(
        displayName = "WireGuard® SE",
        cipher = "ChaCha20-Poly1305",
        description = "Modern, ultra-fast cryptographic tunnel with minimal overhead and instantaneous handshake.",
        badge = "FASTEST"
    ),
    VELLOR_STEALTH(
        displayName = "Vellor Stealth-TLS",
        cipher = "AES-256-GCM + Shadowsocks",
        description = "Proprietary obfuscation layer masking VPN traffic as regular HTTPS browser traffic.",
        badge = "STEALTH"
    ),
    IKEV2(
        displayName = "IKEv2 / IPsec",
        cipher = "AES-256-CBC + SHA384",
        description = "Resilient protocol with seamless mobile roaming and reconnection during network handoffs.",
        badge = "STABLE"
    ),
    OPENVPN_UDP(
        displayName = "OpenVPN UDP",
        cipher = "AES-256-GCM",
        description = "Battle-tested industry standard protocol supporting legacy firewalls and complex routing.",
        badge = "LEGACY"
    )
}
