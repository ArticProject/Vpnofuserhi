package com.example.vpn

import android.app.Application
import android.content.Intent
import android.net.VpnService
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.model.VpnState
import com.example.service.VellorVpnService
import com.example.viewmodel.VpnViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Opt-in device check. Grant ACTIVATE_VPN appop on the disposable emulator first. */
@RunWith(AndroidJUnit4::class)
class NativeVpnTest {
    @Test fun realCoreStartsProbesAndStops() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNull("Grant Android VPN permission before this device test", VpnService.prepare(context))
        val server = kotlinx.coroutines.runBlocking {
            com.example.subscription.H1SubscriptionClient().load(
                "TEST", com.example.subscription.SubscriptionStorage(context).deviceId
            ).servers.first()
        }
        val intent = Intent(context, VellorVpnService::class.java)
            .setAction(VellorVpnService.ACTION_CONNECT)
            .putExtra(VellorVpnService.EXTRA_SERVER_NAME, server.fullName)
            .putExtra(VellorVpnService.EXTRA_VLESS_URL, server.vlessUrl)
        try {
            ContextCompat.startForegroundService(context, intent)
            val deadline = System.nanoTime() + 90_000_000_000
            while (System.nanoTime() < deadline && !VellorVpnService.isRunning &&
                !VellorVpnService.connectionFailed.value) Thread.sleep(100)
            assertEquals("Native service must complete its proxy HTTPS probe",
                VpnState.CONNECTED, VellorVpnService.connectionState.value)
            assertTrue(VellorVpnService.isRunning)
            Log.i("VellorNativeTest", "VPN_READY_FOR_EXTERNAL_PROBE")
            // Allows a separate UID (adb shell/browser) to exercise actual TUN routing.
            Thread.sleep(20_000)
            assertTrue(VellorVpnService.isRunning)
        } finally {
            context.startService(Intent(context, VellorVpnService::class.java)
                .setAction(VellorVpnService.ACTION_DISCONNECT))
        }
        val deadline = System.nanoTime() + 30_000_000_000
        while (System.nanoTime() < deadline && VellorVpnService.isRunning) Thread.sleep(100)
        assertFalse(VellorVpnService.isRunning)
    }
}
