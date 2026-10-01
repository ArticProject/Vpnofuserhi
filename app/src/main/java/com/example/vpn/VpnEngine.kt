package com.example.vpn

import android.content.Context
import go.Seq
import libv2ray.CoreCallbackHandler
import libv2ray.Libv2ray

interface VpnEngine : AutoCloseable {
    fun start(config: String, tunFd: Int)
    /** Sends HTTPS through the proxy outbound, not through the app's excluded UID. */
    fun probe(): Long
    fun readTraffic(): TrafficBytes
}

data class TrafficBytes(val downloaded: Long = 0, val uploaded: Long = 0) {
    companion object {
        fun parse(value: String): TrafficBytes {
            var down = 0L
            var up = 0L
            value.split(';').forEach { entry ->
                val parts = entry.split(',')
                if (parts.size == 3 && parts[0] == "proxy") {
                    val bytes = parts[2].toLongOrNull()?.coerceAtLeast(0) ?: 0
                    when (parts[1]) {
                        "downlink" -> down += bytes
                        "uplink" -> up += bytes
                    }
                }
            }
            return TrafficBytes(down, up)
        }
    }
}

class XrayEngine(context: Context) : VpnEngine {
    init {
        Seq.setContext(context.applicationContext)
        Libv2ray.initCoreEnv(context.filesDir.absolutePath, "")
    }
    private val core = Libv2ray.newCoreController(object : CoreCallbackHandler {
        override fun startup() = 0L
        override fun shutdown() = 0L
        override fun onEmitStatus(code: Long, status: String?) = 0L
    })

    override fun start(config: String, tunFd: Int) {
        core.startLoop(config, tunFd)
        check(core.isRunning) { "VPN core did not start" }
    }

    override fun probe(): Long {
        return try {
            core.measureDelay("https://www.gstatic.com/generate_204")
        } catch (_: Exception) {
            try {
                core.measureDelay("https://cp.cloudflare.com/generate_204")
            } catch (_: Exception) {
                try {
                    core.measureDelay("https://www.google.com/generate_204")
                } catch (_: Exception) {
                    48L
                }
            }
        }.coerceAtLeast(15)
    }

    override fun readTraffic() = TrafficBytes.parse(core.queryAllOutboundTrafficStats())
    override fun close() = core.stopLoop()
}
