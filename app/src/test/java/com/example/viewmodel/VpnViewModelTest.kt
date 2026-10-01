package com.example.viewmodel

import android.content.Intent
import com.example.subscription.*
import com.example.service.VellorVpnServiceTest
import kotlinx.coroutines.CompletableDeferred
import androidx.lifecycle.ViewModelStore
import com.example.model.VpnState
import com.example.service.VellorVpnService
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowVpnService
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VpnViewModelTest {
    private lateinit var model: VpnViewModel
    private val store = ViewModelStore()
    private val key = "00000000-0000-0000-0000-000000000001"
    private var failure = false
    private var pending: CompletableDeferred<Subscription>? = null
    private val sample get() = H1SubscriptionClient.parse(VellorVpnServiceTest.PROFILE, "upload=0;download=0;expire=4102444800")
    private fun activate() {
        model.activateKey(key)
        ShadowLooper.shadowMainLooper().idle()
        assertTrue(model.isActivated.value)
    }

    @Before fun setUp() {
        VellorVpnService.connectionState.value = VpnState.DISCONNECTED
        VellorVpnService.connectionFailed.value = false
        RuntimeEnvironment.getApplication().getSharedPreferences("vellor_access", 0).edit().clear().commit()
        model = VpnViewModel(RuntimeEnvironment.getApplication(), SubscriptionProvider { _, _ ->
            if (failure) throw SubscriptionException("Access denied")
            pending?.await() ?: sample
        })
        store.put("vpn", model)
    }

    @After fun tearDown() {
        store.clear()
        VellorVpnService.connectionState.value = VpnState.DISCONNECTED
    }

    @Test fun permissionLauncherFailureDoesNotStartService() {
        model.onVpnPermissionFailed()
        assertEquals(VpnState.DISCONNECTED, model.vpnState.value)
        assertTrue(model.connectionFailed.value)
        assertNull(shadowOf(RuntimeEnvironment.getApplication()).nextStartedService)
    }

    @Test fun permissionRevokedBeforeCallbackDoesNotStartService() {
        activate()
        ShadowVpnService.setPrepareResult(Intent("permission_required"))
        model.onVpnPermissionGranted()
        ShadowLooper.shadowMainLooper().idle()
        assertEquals(VpnState.DISCONNECTED, model.vpnState.value)
        assertTrue(model.connectionFailed.value)
        assertNull(shadowOf(RuntimeEnvironment.getApplication()).nextStartedService)
    }

    @Test fun stateWaitsForServiceInsteadOfTimer() {
        activate()
        ShadowVpnService.setPrepareResult(null)
        model.toggleConnect()
        ShadowLooper.shadowMainLooper().idle()
        val intent = shadowOf(RuntimeEnvironment.getApplication()).nextStartedService
        assertEquals(VellorVpnService.ACTION_CONNECT, intent.action)
        ShadowLooper.shadowMainLooper().idleFor(Duration.ofSeconds(2))
        assertEquals(VpnState.CONNECTING, model.vpnState.value)
        assertEquals(0f, model.downloadSpeedMb.value)

        VellorVpnService.connectionState.value = VpnState.DISCONNECTED
        VellorVpnService.connectionFailed.value = true
        ShadowLooper.shadowMainLooper().idle()
        assertEquals(VpnState.DISCONNECTED, model.vpnState.value)
        assertTrue(model.connectionFailed.value)
    }

    @Test fun disconnectCommandsBoundVpnToCloseItsInterface() {
        VellorVpnService.connectionState.value = VpnState.CONNECTED
        model.toggleConnect()
        assertEquals(VellorVpnService.ACTION_DISCONNECT,
            shadowOf(RuntimeEnvironment.getApplication()).nextStartedService.action)
    }

    @Test fun permissionCancellationRemainsDisconnected() {
        activate()
        ShadowVpnService.setPrepareResult(Intent("permission_required"))
        model.toggleConnect()
        assertNotNull(model.vpnPermissionIntent.value)
        model.onVpnPermissionDenied()
        assertNull(model.vpnPermissionIntent.value)
        assertEquals(VpnState.DISCONNECTED, model.vpnState.value)
        assertNull(shadowOf(RuntimeEnvironment.getApplication()).nextStartedService)
    }
    @Test fun connectWithoutActivationOpensDialogWithoutStartingService() {
        ShadowVpnService.setPrepareResult(null)
        model.toggleConnect()
        assertTrue(model.showActivationDialog.value)
        assertNull(shadowOf(RuntimeEnvironment.getApplication()).nextStartedService)
        assertTrue(model.servers.value.isEmpty())
    }

    @Test fun legacyLocalKeyDoesNotActivate() {
        model.activateKey("VELLOR-VIP")
        assertFalse(model.isActivated.value)
        assertNotNull(model.activationError.value)
    }

    @Test fun serverDenialDoesNotActivateOrSaveKey() {
        failure = true
        model.activateKey(key)
        ShadowLooper.shadowMainLooper().idle()
        assertFalse(model.isActivated.value)
        assertEquals("", SubscriptionStorage(RuntimeEnvironment.getApplication()).key)
        assertFalse(model.activationBusy.value)
    }

    @Test fun logoutWhileActivationPendingCannotRestoreAccess() {
        pending = CompletableDeferred()
        model.activateKey(key)
        ShadowLooper.shadowMainLooper().idle()
        assertTrue(model.activationBusy.value)
        model.deactivateKey()
        pending!!.complete(sample)
        ShadowLooper.shadowMainLooper().idle()
        assertFalse(model.isActivated.value)
        assertTrue(model.servers.value.isEmpty())
        assertEquals("", SubscriptionStorage(RuntimeEnvironment.getApplication()).key)
    }

    @Test fun denialAtConnectDoesNotUsePreviouslyLoadedProfile() {
        activate()
        ShadowVpnService.setPrepareResult(null)
        failure = true
        model.toggleConnect()
        ShadowLooper.shadowMainLooper().idle()
        assertNull(shadowOf(RuntimeEnvironment.getApplication()).nextStartedService)
        assertEquals(VpnState.DISCONNECTED, model.vpnState.value)
        assertTrue(model.connectionFailed.value)
    }

    @Test fun cancelledConnectCannotStartAfterSubscriptionArrives() {
        activate()
        ShadowVpnService.setPrepareResult(null)
        pending = CompletableDeferred()
        model.toggleConnect()
        ShadowLooper.shadowMainLooper().idle()
        model.toggleConnect()
        pending!!.complete(sample)
        ShadowLooper.shadowMainLooper().idle()
        val shadow = shadowOf(RuntimeEnvironment.getApplication())
        assertEquals(VellorVpnService.ACTION_DISCONNECT, shadow.nextStartedService.action)
        assertNull(shadow.nextStartedService)
    }

    @Test fun activationLoadsOnlySubscriptionServersAndLogoutClearsThem() {
        activate()
        assertEquals(1, model.servers.value.size)
        assertEquals(sample.servers.first().vlessUrl, model.selectedServer.value.vlessUrl)
        model.deactivateKey()
        assertTrue(model.servers.value.isEmpty())
        assertEquals("", model.selectedServer.value.vlessUrl)
    }

    @Test fun batterySaverToggleUpdatesStateAndPersists() {
        assertTrue(model.isBatterySaverEnabled.value)
        model.toggleBatterySaver(false)
        assertFalse(model.isBatterySaverEnabled.value)
        model.toggleBatterySaver(true)
        assertTrue(model.isBatterySaverEnabled.value)
    }

}
