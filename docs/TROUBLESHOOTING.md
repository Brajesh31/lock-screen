# Troubleshooting

This guide covers common setup, build, integration, and runtime failures.

## 1. Gradle cannot find the Android SDK

**Symptoms:** Gradle reports that the SDK location is missing or no Android platform is installed.

**Resolution:**

1. Install Android SDK Platform 30 and Build Tools 30.0.3.
2. Create `local.properties` in the repository root.
3. Set `sdk.dir` to the local SDK directory.
4. Re-run Gradle sync.

Do not commit the local SDK path.

## 2. Gradle version is incompatible

The current baseline uses Android Gradle Plugin 4.2.2 with Gradle 6.7.1 and JDK 11.

Check:

```bash
java -version
gradle --version
```

Do not run the project with an arbitrary current Gradle release. A newer Gradle version may reject old Android plugin APIs.

## 3. Dependency resolution fails

The build uses Google Maven and Maven Central. Confirm that the build has network access and that the configured repositories are reachable.

If a dependency cannot be resolved:

- Record the exact group, artifact, and version.
- Confirm the artifact exists in the configured repository.
- Check whether the dependency was moved or retired.
- Avoid adding JCenter as a quick workaround.
- Do not silently upgrade the entire Android toolchain to fix one artifact.

## 4. Missing local.properties

The project intentionally ignores `local.properties`. Android Studio usually creates it. For command-line builds, add the SDK path manually as documented in [Build and release](BUILD_AND_RELEASE.md).

## 5. Sample app opens but activity cannot be found

Confirm that the host manifest declares:

```xml
<activity
    android:name="com.amirarcane.lockscreen.activity.EnterPinActivity"
    android:exported="false"
    android:theme="@style/Theme.AppCompat.NoActionBar" />
```

Also confirm that the host app includes the `:lockscreen` module dependency.

## 6. Theme or resource inflation exception

The activity uses AppCompat resources and a custom keypad view. Check that the host app uses compatible Support Library 28.0.0 dependencies and an AppCompat theme.

Do not mix AndroidX and support-library versions without completing a migration.

## 7. PIN screen is blank on an older device

The keypad should initialize on all supported API levels. Fingerprint resources are loaded only on API 23 or later. If the screen is blank, capture the full exception and verify that the host app is using the current library commit rather than an older build artifact.

## 8. Fingerprint icon is hidden

This is expected when any of the following is true:

- Android API is below 23.
- FingerprintManager service is unavailable.
- Device has no fingerprint sensor.
- No fingerprint is enrolled.
- The app permission is missing.
- Device lock screen is not secured.
- Keystore or cipher initialization fails.

PIN entry remains available. Fingerprint availability must never be assumed from the device model alone.

## 9. Fingerprint authentication stops after backgrounding

The activity cancels authentication in `onPause`. When it resumes, it starts a new request if the manager and CryptoObject are still available. If the device reports an authentication error, use PIN entry and capture the sanitized platform message.

## 10. PIN verification fails after an update

Older versions stored a SHA-256 digest. The current code supports a one-time upgrade when the correct legacy PIN is entered.

If app data was cleared, the old preference no longer exists and the user must configure a new PIN. Do not attempt to recover the PIN from the digest.

## 11. Font does not load

Font paths are relative to the host app's assets directory, not the library's resource directory. Confirm the exact path and file name. The file must be included in the host application's `src/main/assets` folder.

## 12. Custom keypad order throws an exception

A custom key set must contain ten unique decimal digits, each from zero through nine. Values cannot be duplicated, omitted, negative, or greater than nine.

## 13. Lint reports a deprecated API

The project retains legacy Android Support Library and FingerprintManager APIs. These warnings are known migration work. Review the exact lint issue and document any temporary suppression. Do not suppress all lint checks globally.

## 14. What to include in a bug report

Include:

- Android version and API level.
- Device or emulator model.
- JDK and Gradle versions.
- Build variant and exact Gradle task.
- Repository commit SHA.
- Full stack trace or workflow log.
- Steps to reproduce.
- Whether the issue affects the sample app or a consuming app.

Remove PIN values, personal information, signing data, tokens, and other secrets before sharing logs.
