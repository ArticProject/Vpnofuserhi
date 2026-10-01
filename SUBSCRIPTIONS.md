# H1Cloud subscriptions

Vellor 1.2 uses the panel's public subscription endpoint. No administrator API token is
included in the application. Personal activation accepts either the client's UUID or
its complete `http://de1.h1cloud.net:25557/sub/<uuid>` subscription URL. Client names
(such as Matvey) are not passwords. Arbitrary memorable aliases require a separate
lookup service and are not implemented.

## Owner workflow

1. Create one panel client per person, choose their inbound(s), expiry and traffic.
2. Set the client device limit to **1** for access bound to one installation.
3. Give the person their **subscription URL**, using the copy button in the panel.
4. They paste it in Vellor's existing Profile activation field, then connect.
5. For a new phone or app reinstall, reset that client's devices in the panel.
   Signing out alone preserves the installation identifier and does not revoke the
   subscription. Ban or delete the client in the panel to revoke access.

The panel accepts `X-HWID`. A controlled temporary-client probe confirmed that the
first identifier and its repeated requests receive the subscription, while a different
identifier receives HTTP403 with device limit1. The temporary client was deleted.
This is device binding for subscription retrieval, **not cryptographic proof of a
physical device or guaranteed protection against deliberate HWID/credential copying**.
One shared VPN credential with offline activation codes cannot provide independent
revocation, quotas or globally single-use activation.

## App behavior

- A random installation UUID lives in Android's no-backup directory. Access preferences
  are excluded from cloud backup and device transfer; backup is disabled for the app.
- A saved personal key is revalidated at app startup and before every connection.
  While the app ViewModel is alive, it also rechecks every60 seconds during a connection. Failed checks stop the tunnel;
  there is no fallback to the previous shared profile or offline local activation list.
  Closing the activity can stop those periodic app checks while the VPN service continues;
  ongoing expiry/revocation enforcement therefore also depends on the provider server.
- Subscriptions may be base64 or plain text containing VLESS TCP/REALITY links.
  Unsupported links are filtered; an empty supported list fails activation.
- Expiry and traffic come from `Subscription-Userinfo`. Missing limits are shown as
  unspecified, not invented. The public endpoint does not supply the device limit.
- Requests are bounded, time out, reject redirects and accept only the configured
  provider URL. Neither response bodies nor private keys are logged.
- Server lists come from the subscription. The previous embedded Spain/Poland profiles
  and the embedded administrator activation token have been removed.
- Shared `TEST` maps to the separately created `vellor_trial` client. Its initial
  allowance is **1day from creation and1GB shared across everyone**, not per user.
  The owner can extend/edit this client in the panel without updating the app. Deleting
  and recreating it changes its UUID and requires updating the public trial mapping.

## Transport limitation

The provider currently serves subscriptions over HTTP. Verified TLS attempts failed:
TLS is unavailable on port25557 and port443's certificate does not match the hostname.
Android's cleartext exception is limited to `de1.h1cloud.net`. HTTP exposes subscription
credentials and permits tampering on the network; it is unsuitable for a secure public
subscription distribution service. Ask the provider for a valid HTTPS subscription
endpoint before public distribution, then update the URL validator and network policy.
Administrative API credentials must stay outside the app even after HTTPS is enabled.
The old administrator token was present in earlier APK/source; rotate it in the panel.
The original shared vellor_vip client is still enabled so the previous app keeps working
until the owner verifies their personal subscription. Afterwards revoke that shared
client in the panel: older APKs still contain its working VPN credential.

## Verification

Robolectric tests cover subscription parsing, expiry/quota denial, URL restrictions,
request HWID, redirects, response size, persistent installation identity, activation
and connection cancellation, missing activation and revalidation failure. Native Xray
routing was verified in the prior build; this version's complete activation-to-tunnel
flow still needs a phone/device check. The instrumentation smoke test uses the shared
TEST subscription and requires Android VPN permission; it is opt-in and needs a valid
unexpired trial.
