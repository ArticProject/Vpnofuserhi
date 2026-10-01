# Vellor VPN engine

This version uses the official **AndroidLibXrayLite v26.9.9** AAR, with Xray-core
26.9.9, commit `52a412d9e2f5c2a5142b1b4e2ab3771dacb8b120`.
The underlying Xray release is marked prerelease upstream. The dependency is pinned;
upgrading it requires rechecking the TUN, callback and statistics interfaces.

Sources and corresponding releases:
- https://github.com/2dust/AndroidLibXrayLite/tree/v26.9.9 (LGPL-3.0)
- https://github.com/XTLS/Xray-core/tree/52a412d9e2f5c2a5142b1b4e2ab3771dacb8b120 (MPL-2.0)
- https://github.com/2dust/AndroidLibXrayLite/releases/tag/v26.9.9

License texts are included in `app/src/main/assets/licenses`. The library is used
unmodified. App sources and the Gradle dependency path permit replacing/relinking
it; the upstream wrapper README documents building its AAR from source.

## Build

Use Java 21, Gradle 9.3.1 and Android SDK 36/build-tools 36.0.0. `prepareXray`
downloads the AAR from the official release and checks SHA256
`9ecf4c921568d8f4cb8550d3bafe08ff6f1d1984f45a6ad183dcdf52ee9302de`.
First build requires internet access. No NDK/Go toolchain is needed when using this
verified upstream AAR. Country databases are not packaged: this app uses no geo rules.

```sh
./gradlew --project-cache-dir /tmp/vellor-gradle :app:testDebugUnitTest :app:assembleDebug :app:lintDebug :app:assembleRelease
```

APK variants: arm64-v8a (most current phones), armeabi-v7a (older 32-bit phones),
x86_64 (emulators). Release artifacts are unsigned; sign with your own Android key
for distribution. A locally signed test APK is not a production release.

## Behavior and limits

- Supports UUID VLESS profiles with TCP/raw transport and REALITY, optional Vision.
  Unsupported parameters are rejected rather than silently ignored.
- Both IPv4 and IPv6 default routes and DNS enter the TUN. Xray has one VLESS
  outbound and no direct fallback. The Vellor UID is excluded to prevent its own
  transport connections looping through TUN; user applications remain covered.
- Android grants VPN permission; the foreground service creates TUN and passes its
  descriptor to Xray. Java closes the descriptor after core shutdown.
- Disconnect sends ACTION_DISCONNECT: Android binds an established VpnService, so
  stopService alone cannot destroy it until TUN closes.
- Core startup, probes, stats and shutdown run on one worker thread. Request
  generations prevent delayed startup results from restoring a cancelled session.
- CONNECTED requires a successful HTTPS probe through Xray to gstatic or Cloudflare.
  Blocking both probe endpoints can reject a usable server. A later network outage
  may stall traffic until reconnect; CONNECTED is not a continuous availability test.
- Speeds/session byte totals come from Xray outbound counters. Ping is the startup
  HTTPS probe time, not synthetic latency. Switching networks may require reconnect.
- WireGuard, OpenVPN, IKEv2, double-hop and ad filtering are not implemented; controls
  explain their unavailable state. REALITY is active. For a persistent kill switch,
  enable Android's Always-on VPN and block connections without VPN in VPN settings.
- Personal profiles now come from H1Cloud subscriptions. See SUBSCRIPTIONS.md for
  activation, device binding, the shared trial and provider HTTP limitations. Never log
  subscription codes, URLs or VLESS configurations.

## Tests

Robolectric tests exercise configuration validation, counters, permission failure,
foreground failure, failed server probe, cancellation during startup and cleanup.
They use a fake native engine and cannot prove real device routing. Native Android
and live server checks must be reported separately from these tests.
