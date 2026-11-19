# NETGUARD

Android network monitoring, diagnostics and optimisation app. See
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the architecture, directory
structure, permissions model, design system, and the limits/risks of Android
network optimisation.

**Status:** Phase 1 (scaffold, theme, navigation) and Phase 2 (connectivity
monitoring) are implemented. Phases 3–6 (speed/latency/DNS testing,
optimisation, analytics/history, background monitoring, polish) are not yet
built.

## Build and download the APK

### Requirements

- JDK 17
- Android SDK Platform 35 and Build Tools 35.0.0
- Gradle 8.14.3 (provided by the Gradle wrapper)

### Build locally

Run the unit tests, Android lint, and debug APK build:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

On Windows, run `gradlew.bat testDebugUnitTest lintDebug assembleDebug`.

### Build on GitHub

The workflow in `.github/workflows/android.yml` runs on pushes to `main`,
pull requests targeting `main`, and manual dispatch. To start it manually, open
the repository's **Actions** tab, select **Android**, then choose **Run
workflow**.

After a successful run, the APK is at
`app/build/outputs/apk/debug/app-debug.apk` in the build job and is uploaded as
the `netguard-debug-apk` artifact for 14 days. In GitHub, open the completed
**Android** workflow run and download that artifact.
