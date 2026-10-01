package com.example.service

import com.example.vpn.VpnEngine
import com.example.vpn.TrafficBytes
import com.example.model.VpnState
import org.robolectric.shadows.ShadowLooper
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.ParcelFileDescriptor
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ServiceController
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.RealObject
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowService
import org.robolectric.shadows.ShadowVpnService

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], shadows = [TestVpnBuilder::class])
class VellorVpnServiceTest {
    private lateinit var controller: ServiceController<VellorVpnService>
    private lateinit var service: VellorVpnService
    private lateinit var pipe: Array<ParcelFileDescriptor>
    private lateinit var shadow: ShadowService

    @Before fun setUp() {
        VellorVpnService.engineFactory = { object : VpnEngine {
            override fun start(config: String, tunFd: Int) {}
            override fun probe() = 25L
            override fun readTraffic() = TrafficBytes()
            override fun close() {}
        } }
        ShadowVpnService.setPrepareResult(null)
        pipe = ParcelFileDescriptor.createPipe()
        TestVpnBuilder.descriptor = pipe[0]
        TestVpnBuilder.establishCalls = 0
        TestVpnBuilder.routes.clear()
        TestVpnBuilder.excludedApps.clear()
        controller = Robolectric.buildService(VellorVpnService::class.java).create()
        service = controller.get()
        shadow = Shadow.extract(service)
    }

    @After fun tearDown() {
        controller.destroy()
        await { VellorVpnService.connectionState.value == VpnState.DISCONNECTED }
        VellorVpnService.engineFactory = { com.example.vpn.XrayEngine(it) }
        pipe.forEach { it.close() }
    }

    private fun await(done: () -> Boolean) {
        val deadline = System.nanoTime() + 5_000_000_000
        while (!done() && System.nanoTime() < deadline) {
            ShadowLooper.shadowMainLooper().idle()
            Thread.sleep(10)
        }
        ShadowLooper.shadowMainLooper().idle()
        assertTrue("Timed out waiting for service worker", done())
    }

    private fun connect() {
        service.onStartCommand(Intent(service, VellorVpnService::class.java)
            .setAction(VellorVpnService.ACTION_CONNECT)
            .putExtra(VellorVpnService.EXTRA_VLESS_URL, PROFILE), 0, 1)
        await { VellorVpnService.connectionState.value == VpnState.CONNECTED || shadow.isStoppedBySelf }
    }

    companion object {
        const val PROFILE = "vless://00000000-0000-4000-8000-000000000001@192.0.2.1:443?security=reality&type=tcp&encryption=none&sni=example.com&fp=chrome&pbk=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA&sid=abcd"
    }

    @Test fun connectingKeepsServiceAlive() {
        connect()
        assertFalse("Connect must not call stopSelf", shadow.isStoppedBySelf)
        assertTrue(VellorVpnService.isRunning)
        assertNotNull(shadow.lastForegroundNotification)
        assertEquals(setOf("0.0.0.0/0", "::/0"), TestVpnBuilder.routes.toSet())
        assertEquals(listOf(service.packageName), TestVpnBuilder.excludedApps)
    }

    @Test fun foregroundFailureDoesNotEstablishTunnel() {
        shadow.setThrowInStartForeground(SecurityException("Foreground rejected"))
        connect()
        assertEquals(0, TestVpnBuilder.establishCalls)
        assertFalse(VellorVpnService.isRunning)
        assertTrue(shadow.isStoppedBySelf)
    }

    @Test fun revokedPermissionCannotBecomeConnected() {
        TestVpnBuilder.descriptor = null
        connect()
        assertFalse("establish() may return null after permission is revoked",
            VellorVpnService.isRunning)
        assertTrue(shadow.isStoppedBySelf)
        assertNull(shadow.lastForegroundNotification)
    }

    @Test fun permissionRequiredDoesNotEstablishTunnel() {
        ShadowVpnService.setPrepareResult(Intent("permission_required"))
        connect()
        assertEquals(0, TestVpnBuilder.establishCalls)
        assertFalse(VellorVpnService.isRunning)
    }

    @Test fun disconnectRemovesForegroundAndClosesInterface() {
        connect()
        service.onStartCommand(Intent().setAction(VellorVpnService.ACTION_DISCONNECT), 0, 2)
        await { shadow.isStoppedBySelf }
        assertFalse(VellorVpnService.isRunning)
        assertTrue(shadow.isStoppedBySelf)
        assertNull(shadow.lastForegroundNotification)
        assertThrows(IllegalStateException::class.java) { pipe[0].fd }
    }

    @Test fun failedServerProbeDoesNotBecomeConnected() {
        VellorVpnService.engineFactory = { object : VpnEngine {
            override fun start(config: String, tunFd: Int) {}
            override fun probe(): Long = throw java.io.IOException("offline")
            override fun readTraffic() = TrafficBytes()
            override fun close() {}
        } }
        connect()
        assertFalse(VellorVpnService.isRunning)
        assertTrue(VellorVpnService.connectionFailed.value)
        assertThrows(IllegalStateException::class.java) { pipe[0].fd }
    }

    @Test fun disconnectDuringProbeCannotPublishConnectedLater() {
        val entered = java.util.concurrent.CountDownLatch(1)
        val resume = java.util.concurrent.CountDownLatch(1)
        VellorVpnService.engineFactory = { object : VpnEngine {
            override fun start(config: String, tunFd: Int) {}
            override fun probe(): Long { entered.countDown(); resume.await(3, java.util.concurrent.TimeUnit.SECONDS); return 20 }
            override fun readTraffic() = TrafficBytes()
            override fun close() {}
        } }
        service.onStartCommand(Intent().setAction(VellorVpnService.ACTION_CONNECT)
            .putExtra(VellorVpnService.EXTRA_VLESS_URL, PROFILE), 0, 1)
        assertTrue(entered.await(3, java.util.concurrent.TimeUnit.SECONDS))
        service.onStartCommand(Intent().setAction(VellorVpnService.ACTION_DISCONNECT), 0, 2)
        resume.countDown()
        await { shadow.isStoppedBySelf }
        assertEquals(VpnState.DISCONNECTED, VellorVpnService.connectionState.value)
        assertFalse(VellorVpnService.isRunning)
    }

    @Test fun manifestDeclaresVpnForegroundServiceTypeAndPermission() {
        val app = RuntimeEnvironment.getApplication()
        val info = app.packageManager.getServiceInfo(
            ComponentName(app, VellorVpnService::class.java), 0)
        assertEquals(ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED,
            info.foregroundServiceType)
        val permissions = app.packageManager.getPackageInfo(
            app.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions.orEmpty().toList()
        assertTrue(permissions.contains("android.permission.FOREGROUND_SERVICE_SYSTEM_EXEMPTED"))
    }
}

@Implements(VpnService.Builder::class)
class TestVpnBuilder {
    @RealObject private lateinit var builder: VpnService.Builder
    // Android uses InetAddress.parseNumericAddress, which is absent on the JVM.
    // These stubs isolate the service lifecycle from native TUN configuration.
    @Implementation fun addAddress(address: String, prefixLength: Int): VpnService.Builder = builder
    @Implementation fun addRoute(address: String, prefixLength: Int): VpnService.Builder {
        routes.add("$address/$prefixLength")
        return builder
    }
    @Implementation fun addDnsServer(address: String): VpnService.Builder = builder
    @Implementation fun addDisallowedApplication(packageName: String): VpnService.Builder {
        excludedApps.add(packageName)
        return builder
    }

    companion object {
        val routes = mutableListOf<String>()
        val excludedApps = mutableListOf<String>()
        var descriptor: ParcelFileDescriptor? = null
        var establishCalls = 0
    }
    @Implementation fun establish(): ParcelFileDescriptor? {
        establishCalls++
        return descriptor
    }
}
