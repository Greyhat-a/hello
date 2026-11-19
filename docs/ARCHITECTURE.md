# NETGUARD — Architecture & Design Brief (v1.0)

Status: Phases 1–2 implemented (scaffold, theme, navigation, connectivity
monitoring). Phases 3–6 are specified here and not yet built. See the root
[README](../README.md) for local build requirements and the GitHub Actions APK
workflow.

---

## 1. Proposed architecture

**Pattern:** MVVM + a thin domain layer, unidirectional data flow.

```
Compose UI  ──events──▶  ViewModel  ──calls──▶  UseCase / Engine (domain)
    ▲                        │                         │
    └──── StateFlow<UiState>─┘                         ▼
                                              Repository interfaces (domain)
                                                       │ implemented by
                                        ┌──────────────┴──────────────┐
                                     data/ (Room, DataStore)     network/ (OkHttp, ConnectivityManager,
                                                                  WifiManager, TelephonyManager)
```

**Layers and rules**

| Layer | Contains | May depend on |
|---|---|---|
| `presentation` | Compose screens, ViewModels, theme, shared components, navigation | `domain` |
| `core` / `domain` | Measurement type, models, repository interfaces, engines' contracts, scoring logic (pure Kotlin) | nothing Android-specific |
| `data` | Room, DataStore, repository implementations | `domain` |
| `network` | NetworkMonitor, SpeedTestEngine, LatencyEngine, DnsAnalyzer, SignalAnalyzer | `domain` |
| `work` | WorkManager workers, foreground service, boot receiver | `domain`, `data`, `network` |
| `di` | Hilt modules | all |

**Key decision — the `Measurement<T>` type** (`core/Measurement.kt`). Every value shown in the app is
exactly one of `Measured`, `Estimated`, `Unavailable`, `Failed`, or `NotPermitted`. This makes the rules "never invent measurements" and
"distinguish failed tests from tests Android does not permit" a compile-time property
rather than a convention.

**Single Gradle module for now.** Package boundaries enforce layering. The pure-Kotlin
engines (scoring, jitter/loss math) can be extracted into a `:core` module later if build
times or testing isolation justify it. Splitting now would add Gradle overhead with no benefit.

---

## 2. Directory structure

```
netguard/
├── build.gradle.kts · settings.gradle.kts · gradle.properties
├── gradle/libs.versions.toml            # single source of dependency versions
├── docs/ARCHITECTURE.md
└── app/
    ├── build.gradle.kts · proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── res/values/ (strings, themes)
        │   └── java/com/netguard/app/
        │       ├── NetGuardApp.kt  MainActivity.kt
        │       ├── core/Measurement.kt          # Measured / Estimated / Unavailable / Failed / NotPermitted
        │       ├── presentation/                # (Phase 1, built)
        │       │   ├── theme/        Color, Type, Spacing, Theme
        │       │   ├── components/   MetricTile, StatusPill, EmptyState, SectionHeader
        │       │   ├── navigation/   Destination, NetGuardRoot (bottom bar + NavHost)
        │       │   └── screens/      ScreenScaffold; home/ diagnostics/ optimize/ analytics/ settings/
        │       ├── domain/           # models, repository interfaces, scoring   (Phases 2–4)
        │       ├── data/             # DataStore (Phase 2+), Room (Phase 5)
        │       ├── network/          # monitor, speedtest, latency, dns, signal, diagnostics (Phases 2–4)
        │       ├── work/             # WorkManager, foreground service, boot receiver (Phase 5)
        │       └── di/               # Hilt modules (added with first injectable)
        └── test/                     # JVM unit tests
```

---

## 3. Network-monitoring strategy

| Concern | Mechanism | Cost |
|---|---|---|
| Connectivity state & capabilities | `ConnectivityManager.registerDefaultNetworkCallback` → `callbackFlow`. `NET_CAPABILITY_VALIDATED` = Android's own check that internet is reachable. | Event-driven, near zero |
| Wi-Fi details (RSSI, link speed, frequency) | `NetworkCapabilities.transportInfo` as `WifiInfo` (API 29+; request with `FLAG_INCLUDE_LOCATION_INFO` on 31+) | Event-driven |
| SSID | Requires location permission **and** location services on. Otherwise `Unavailable(PermissionDenied)`. | — |
| Cellular signal | `TelephonyManager.signalStrength` (API 28+), network type via `ServiceState`/`NetworkCapabilities`. Needs `READ_PHONE_STATE` for some fields; degrade gracefully. | Low |
| Latency / jitter / loss | Small timed HTTPS `HEAD` (or TCP connect) probes to configurable endpoints. ICMP ping via `Runtime.exec("ping")` is unreliable across OEMs, so it is not a dependency. **Packet loss measured over HTTPS/TCP probes is "probe failure rate", labelled as such, not true ICMP loss.** Jitter = mean absolute difference between consecutive RTTs (RFC 3550 style). | A few KB per run |
| Throughput | Fixed-size multi-stream download/upload to configurable endpoint; user-initiated, or scheduled at a user-set low frequency, **never continuous**. Metered connections warn before running. | MBs per run |
| DNS | `DnsResolver` (API 29+) / `InetAddress` timed lookups against system resolver; optional comparison via direct UDP query to chosen public resolvers (labelled as a comparison, not what the device uses). | Tiny |
| Gateway reachability | Gateway IP from `LinkProperties`; TCP connect probe to it. ICMP to the gateway often blocked → reported "unavailable", not "failed". | Tiny |
| History | Each measurement stored as a row with its `Measurement` kind (measured/estimated) | Room |

**Cadence:** passive connectivity events are always free. Active probes run on
WorkManager (min 15 min, user-configurable) or a user-started foreground session.
A foreground service is only used for an explicit "live monitoring" session.

**Network-health score (Phase 3):** weighted from only the metrics that were *measured*;
missing metrics are excluded and the weights re-normalised, and the UI states which
inputs were used. Weights and thresholds are shown to the user in-app.

---

## 4. Android permissions

| Permission | Needed for | When requested |
|---|---|---|
| `INTERNET` | All probes and tests | Install-time (Phase 1) |
| `ACCESS_NETWORK_STATE` | Connectivity monitoring | Install-time (Phase 1) |
| `ACCESS_WIFI_STATE` | Wi-Fi RSSI → signal strength | Install-time (Phase 2) |
| `ACCESS_FINE_LOCATION` | **Only** to read the Wi-Fi SSID (OS requirement). Optional; the app works without it. | Runtime, with an inline "Allow" affordance, only when the user wants the Wi-Fi name shown (Phase 2) |
| `READ_PHONE_STATE` | Cellular signal/network-type detail. Optional. | Not yet requested — deferred to the Signal Analyzer phase rather than shipped against an unverified permission requirement; Phase 2 reports cellular signal as `Unavailable` with that reason |
| `POST_NOTIFICATIONS` (33+) | Alerts and foreground-service notification | Runtime, when notifications enabled |
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_SPECIAL_USE` (34+) | Live monitoring session | Install-time (Phase 5) |
| `RECEIVE_BOOT_COMPLETED` | Re-schedule monitoring after reboot (only if user enabled it) | Install-time (Phase 5) |
| `BIND_VPN_SERVICE` | **Not requested.** VPN-based DNS is deferred; see risks. | — |

**Phase 1 declares no permissions.** Each phase adds only what it uses.

---

## 5. UI/UX design system

**Direction:** professional technical minimalism. Flat surfaces, hairline borders instead
of shadows, restrained radius, one dominant accent, tabular numerals for all metrics.

**Colour tokens**

| Token | Dark (default) | Light |
|---|---|---|
| background | `#0B1020` navy | `#F4F6FA` |
| surface (cards) | `#151B28` charcoal | `#FFFFFF` |
| surfaceRaised | `#1C2333` | `#EBEFF5` |
| outline (hairline) | `#2A3348` | `#D3D9E4` |
| accent (electric blue) | `#3D7BFF` | `#2B62E6` |
| accent secondary (muted cyan) | `#5FB3C4` | `#237C90` |
| text primary | `#E8ECF4` | `#0F1626` |
| text secondary (cool grey) | `#8F9AB0` | `#566177` |
| success | `#3FB27F` | `#2A8A5E` |
| warning (amber) | `#E0A23A` | `#A86F0C` |
| error | `#D9645B` | `#B9403A` |

Blue is used for primary actions, selection and the active nav item only.
Status colours appear only on status pills and values that are actually in that state.

**Spacing:** 4-pt scale — `xs 4 · sm 8 · md 12 · lg 16 · xl 24 · xxl 32`. Screen gutter 16.
**Shape:** cards 10 dp, pills 6 dp, buttons 8 dp. No pill-shaped cards.
**Type scale (system sans, no downloadable fonts):**
metric 26/SemiBold tabular · headline 20/SemiBold · title 15/Medium · body 14/Regular ·
label 11/Medium +0.8 sp tracking, uppercase for section labels.
**Motion:** none beyond Material defaults; no decorative animation.
**Navigation:** bottom bar, five destinations (Home, Diagnostics, Optimize, Analytics, Settings),
always-visible labels, outlined icons.
**Responsiveness:** content max-width 640 dp, centred, on large screens; 2-column metric grid
on phones. Window-size-class adaptive layouts are scheduled for Phase 6.

---

## 6. Limits of Android network optimisation

NETGUARD **cannot**, without root or system privileges:
- Increase ISP-provisioned bandwidth or bypass congestion.
- Change cellular radio parameters or force a band/tower.
- Give its own traffic priority over other apps.
- Toggle Wi-Fi on/off programmatically (blocked for third-party apps since API 29).
- Silently change the system Private DNS setting (requires `WRITE_SECURE_SETTINGS`).
- Read other apps' traffic (and NETGUARD never attempts to).

NETGUARD **can**, legitimately:
- Measure and explain: throughput, latency, jitter, probe loss, DNS time, signal quality.
- Compare DNS resolvers and guide the user to **Settings → Private DNS** with a one-tap deep link.
- Recommend Wi-Fi band/position changes or switching Wi-Fi ↔ mobile data, based on measurements.
- Prompt the user to switch networks (`ACTION_WIFI_SETTINGS` panel), never do it silently.
- Run scheduled, low-cost health checks and alert on sustained degradation.
- *(Optional, later)* local VPN-based DNS, only with explicit consent and a clear explanation
  that all DNS traffic is routed through the app on-device.

"Optimisation" in NETGUARD therefore means **diagnosis plus guided user action**.
No feature will be labelled as an optimisation unless it is implemented and tested.

---

## 7. Technical risks

| Risk | Impact | Mitigation |
|---|---|---|
| OEM background-kill (Xiaomi, Samsung, etc.) | Monitoring silently stops | WorkManager + detect-and-explain; deep link to battery settings |
| Speed-test endpoint availability / ToS | Tests fail or violate terms | Configurable endpoints; default to a documented-for-purpose host; surface "endpoint unreachable" distinctly from "no internet" |
| Throughput measurement accuracy (TCP slow-start, single stream, device CPU) | Misleading numbers | Multi-stream, warm-up discard, report as measured-with-method; show test server and duration |
| Packet loss without ICMP | Cannot claim true loss | Report "probe failure rate" explicitly; mark ICMP-based loss unavailable |
| Location needed for SSID | Privacy friction | Fully optional; graceful `Unavailable(PermissionDenied)` |
| Foreground-service policy (API 34+ types, Play review) | Rejection or crash | Use only for explicit live sessions; declare correct type; justify in Play listing |
| Metered/mobile data cost of tests | User bill shock | Warn on metered networks; data-budget setting; no continuous bandwidth tests |
| Network changes mid-test | Corrupt results | Abort and mark run `Failed(NetworkChanged)`; never store partial results as measured |
| VPN-service DNS | High privacy/complexity | Deferred; not in the first release |
| Dependency versions & SDK drift | Build break | Versions pinned in one catalog; bump and verify in Phase 6 |
| Cannot verify visual quality without a device | Design drift | Screenshot/Compose-preview review in Phase 6 on several sizes |
