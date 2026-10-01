package com.example.vpn

import com.example.service.VellorVpnServiceTest.Companion.PROFILE
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VlessConfigTest {
    @Test fun createsTunWithSingleProxyOutboundAndRealityParameters() {
        val config = JSONObject(VlessConfig.build(PROFILE + "&flow=xtls-rprx-vision&spx=%2Ftest"))
        assertEquals("tun", config.getJSONArray("inbounds").getJSONObject(0).getString("protocol"))
        val outbounds = config.getJSONArray("outbounds")
        assertEquals(1, outbounds.length()) // No direct escape route.
        val proxy = outbounds.getJSONObject(0)
        assertEquals("vless", proxy.getString("protocol"))
        val reality = proxy.getJSONObject("streamSettings").getJSONObject("realitySettings")
        assertEquals("example.com", reality.getString("serverName"))
        assertEquals("/test", reality.getString("spiderX"))
        assertEquals("abcd", reality.getString("shortId"))
        assertEquals("xtls-rprx-vision", proxy.getJSONObject("settings").getJSONArray("vnext")
            .getJSONObject(0).getJSONArray("users").getJSONObject(0).getString("flow"))
    }

    @Test fun rejectsInvalidOrUnsupportedProfilesWithoutEchoingSecrets() {
        listOf(PROFILE.replace("security=reality", "security=none"),
            PROFILE.replace("type=tcp", "type=ws"), PROFILE + "&insecure=1",
            PROFILE + "&security=reality", PROFILE.replace("sid=abcd", "sid=abc"),
            PROFILE.replace(":443?", ":99999?"), "vless://private-secret@bad").forEach {
            val error = assertThrows(IllegalArgumentException::class.java) { VlessConfig.build(it) }
            assertEquals("Unsupported or invalid VLESS/REALITY profile", error.message)
            assertNull(error.cause)
        }
    }

    @Test fun trafficReadsProxyBytesOnly() {
        assertEquals(TrafficBytes(2000, 500), TrafficBytes.parse(
            "proxy,downlink,2000;direct,downlink,99999;proxy,uplink,500;proxy,uplink,-1;broken;"))
    }

    @Test fun bothDefaultServersProduceValidConfigs() {
        val servers = com.example.subscription.H1Access.DEFAULT_SERVERS
        assertEquals(2, servers.size)
        servers.forEach { server ->
            val jsonStr = VlessConfig.build(server.vlessUrl)
            val json = JSONObject(jsonStr)
            assertEquals("tun", json.getJSONArray("inbounds").getJSONObject(0).getString("protocol"))
            val proxy = json.getJSONArray("outbounds").getJSONObject(0)
            assertEquals("vless", proxy.getString("protocol"))
            val reality = proxy.getJSONObject("streamSettings").getJSONObject("realitySettings")
            assertTrue(reality.getString("publicKey").isNotBlank())
            assertTrue(reality.getString("serverName").isNotBlank())
            assertTrue(reality.getString("shortId").isNotBlank())
            val host = proxy.getJSONObject("settings").getJSONArray("vnext").getJSONObject(0).getString("address")
            assertTrue(host.isNotBlank())
        }
    }
}
