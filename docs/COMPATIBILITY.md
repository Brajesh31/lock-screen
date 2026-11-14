# Compatibility

This file records the current Android and build-tool baseline. The project is maintained conservatively to preserve its existing Java API and support-library dependencies while removing obsolete build tooling.

## 1. Build baseline

| Setting | Value | Notes |
| --- | --- | --- |
| Minimum SDK | 17 | Android 4.2 and later |
| Compile SDK | 30 | Android 11 API surface |
| Target SDK | 28 | Retained for current support-library baseline |
| Build Tools | 30.0.3 | Pinned for CI |
| Android Gradle Plugin | 4.2.2 | Replaces 2.3.3 |
| Gradle | 6.7.1 | Compatible with AGP 4.2.2 |
| JDK | 11 | CI toolchain |
| AppCompat | 28.0.0 | Legacy support library |
| RecyclerView | 28.0.0 | Legacy support library |
| ConstraintLayout | 1.1.3 | Legacy support namespace |

## 2. API 17 through API 22

The keypad and PIN verification are available. Fingerprint controls are hidden because the platform FingerprintManager API is not available before API 23.

The old implementation initialized the entire screen inside an API 23 condition. This made the activity unusable on devices that were otherwise within the declared minimum SDK. The current startup sequence initializes PIN entry on every supported API level.

## 3. API 23 and later

Fingerprint authentication can be prepared when the device has compatible hardware, enrolled fingerprints, the declared permission, and a secure system lock screen.

The implementation uses the deprecated platform FingerprintManager API. It does not provide AndroidX BiometricPrompt behavior and should not be treated as a current biometric integration.

## 4. Android Support Library

The Java source and XML layouts use legacy `android.support.*` classes. The configured dependencies remain on Support Library 28.0.0 and ConstraintLayout 1.1.3.

Do not add AndroidX artifacts to the current modules without completing a migration. Mixing the two class hierarchies can create dependency conflicts and runtime failures.

## 5. Planned AndroidX migration

A future major release should:

1. Migrate all `android.support.*` imports to AndroidX.
2. Replace AppCompat, RecyclerView, and ConstraintLayout dependencies.
3. Replace FingerprintManager with AndroidX BiometricPrompt.
4. Update manifest permissions and biometric behavior.
5. Upgrade compileSdk and targetSdk to a current stable API.
6. Upgrade Android Gradle Plugin and Gradle wrapper as a compatible pair.
7. Move to a supported JDK for the selected plugin.
8. Commit a complete Gradle wrapper, including wrapper JAR and checksum.
9. Revisit exported components and manifest merging.
10. Provide a consumer migration guide and compatibility tests.

## 6. Device validation matrix

Before claiming support for a new API level, test:

- Minimum supported API level.
- API 23 or later with fingerprint hardware.
- API 23 or later without fingerprint hardware.
- Hardware present but no enrolled fingerprint.
- Device without a secure system lock screen.
- Back navigation during PIN entry.
- Activity backgrounding during fingerprint authentication.
- Device rotation and process recreation where supported by the host app.

JVM tests cannot validate hardware-backed biometric behavior. Instrumentation and device testing are required.
