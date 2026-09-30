package com.example.model

enum class VpnProtocol(
    val displayName: String,
    val cipher: String,
    val badge: String,
    val description: String
) {
    WIREGUARD(
        displayName = "WireGuard",
        cipher = "ChaCha20-Poly1305",
        badge = "FASTEST",
        description = "Modern, high-performance protocol using state-of-the-art cryptography."
    ),
    IKEV2(
        displayName = "IKEv2 / IPsec",
        cipher = "AES-256-GCM",
        badge = "STABLE",
        description = "Resilient mobile roaming protocol with fast reconnect capabilities."
    ),
    OPENVPN_UDP(
        displayName = "OpenVPN UDP",
        cipher = "AES-256-CBC",
        badge = "AUDITED",
        description = "Battle-tested open standard with industry-grade reliability."
    ),
    VELLOR_STEALTH(
        displayName = "Vellor Stealth (X-Ray)",
        cipher = "Quantum-ChaCha20",
        badge = "ANTI-CENSOR",
        description = "Obfuscated traffic masquerading as standard HTTPS TLS 1.3 handshakes."
    )
}
