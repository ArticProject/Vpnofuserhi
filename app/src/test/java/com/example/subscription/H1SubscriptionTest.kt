package com.example.subscription

import android.util.Base64
import com.example.service.VellorVpnServiceTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.net.HttpURLConnection
import java.net.URL

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class H1SubscriptionTest {
    private val key = "00000000-0000-0000-0000-000000000001"
    private val profile get() = VellorVpnServiceTest.PROFILE

    @Test fun acceptsPersonalCodeAndExactProviderSubscriptionUrl() {
        assertEquals(key, H1Access.normalize("  $key  "))
        assertEquals(key, H1Access.normalize("http://de1.h1cloud.net:25557/sub/$key"))
        assertEquals("TEST", H1Access.normalize("test"))
    }

    @Test fun rejectsArbitraryHostsPathsAndOldLocalKeys() {
        listOf("Matvey", "VELLOR-VIP", "H1-12345", "http://evil.test:25557/sub/$key",
            "http://de1.h1cloud.net:25557/api/clients", "http://de1.h1cloud.net:25557/sub/$key?foo=1",
            "http://user@de1.h1cloud.net:25557/sub/$key", "http://de1.h1cloud.net:25557/sub/../../api",
            "0000-0-0-0-1").forEach { value ->
            assertThrows(SubscriptionException::class.java) { H1Access.normalize(value) }
        }
    }

    @Test fun parsesBase64ProfilesAndRealUsageWithoutInventingLimits() {
        val body = Base64.encodeToString(profile.toByteArray(), Base64.NO_WRAP)
        val sub = H1SubscriptionClient.parse(body, "upload=10; download=20; expire=2000", 1000)
        assertEquals(1, sub.servers.size)
        assertEquals(profile, sub.servers.first().vlessUrl)
        assertEquals(30L, sub.usedBytes)
        assertEquals(2000L, sub.expiresAt)
        assertNull(sub.limitBytes)
    }

    @Test fun rejectsExpiredAndExhaustedSubscriptions() {
        listOf("expire=1000", "total=30;upload=10;download=20").forEach { header ->
            assertThrows(SubscriptionException::class.java) { H1SubscriptionClient.parse(profile, header, 1000) }
        }
    }

    @Test fun rejectsMalformedMetadataAndUnsupportedProfiles() {
        listOf("download=-1", "expire=tomorrow", "upload=9223372036854775807;download=1").forEach { header ->
            assertThrows(SubscriptionException::class.java) { H1SubscriptionClient.parse(profile, header, 1000) }
        }
        assertThrows(SubscriptionException::class.java) { H1SubscriptionClient.parse("<html>Error</html>", null) }
        assertThrows(SubscriptionException::class.java) { H1SubscriptionClient.parse("vless://private-secret@bad", null) }
    }

    @Test fun requestIncludesStableHwidButNeverAdminAuthentication() = runBlocking {
        val connection = Response(URL(H1Access.url(key)), 200, profile)
        val client = H1SubscriptionClient { connection }
        client.load(key, "installation-A")
        assertEquals("installation-A", connection.getRequestProperty("X-HWID"))
        assertNull(connection.getRequestProperty("Authorization"))
        assertNull(connection.getRequestProperty("X-API-Key"))
        assertFalse(connection.instanceFollowRedirects)
        assertTrue(connection.closed)
    }

    @Test fun rejectsDeviceDenialAndRedirectWithoutLeakingCredentials() {
        listOf(403, 302, 404, 500).forEach { status ->
            val connection = Response(URL(H1Access.url(key)), status, profile)
            val error = assertThrows(SubscriptionException::class.java) {
                runBlocking { H1SubscriptionClient { connection }.load(key, "installation-A") }
            }
            assertFalse(error.message.orEmpty().contains(key))
            assertTrue(connection.closed)
        }
    }

    @Test fun oversizedResponsesAreRejected() {
        val connection = Response(URL(H1Access.url(key)), 200, "a".repeat(H1SubscriptionClient.MAX_BYTES + 1))
        assertThrows(SubscriptionException::class.java) {
            runBlocking { H1SubscriptionClient { connection }.load(key, "installation-A") }
        }
        assertTrue(connection.closed)
    }

    @Test fun logoutDoesNotChangeInstallationIdentity() {
        val context = RuntimeEnvironment.getApplication()
        val first = SubscriptionStorage(context)
        first.key = key
        first.key = ""
        val restored = SubscriptionStorage(context)
        assertEquals(first.deviceId, restored.deviceId)
        assertEquals("", restored.key)
        assertTrue(java.io.File(context.noBackupFilesDir, "vellor-device-id").exists())
    }

    private class Response(url: URL, private val status: Int, private val body: String) : HttpURLConnection(url) {
        var closed = false
        override fun connect() {}
        override fun disconnect() { closed = true }
        override fun usingProxy() = false
        override fun getResponseCode() = status
        override fun getInputStream() = body.byteInputStream()
        override fun getHeaderField(name: String): String? = if (name == "Subscription-Userinfo") "expire=4102444800" else null
    }
}
