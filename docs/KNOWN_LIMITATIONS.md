# Known limitations

This file records current limitations that maintainers and consumers should understand before adopting the library.

## Authentication limitations

- The default PIN contains four decimal digits and therefore has low entropy.
- A persistent failed-attempt lockout is not implemented.
- The library stores a local verifier, not a server-side account credential.
- Private SharedPreferences are not hardware-backed secret storage.
- The host application must enforce access control after successful activity results.
- The library does not encrypt host application data or backups.

## Biometric limitations

- Fingerprint support uses the deprecated platform FingerprintManager API.
- Fingerprint support is available only from API 23.
- Face, iris, and other biometric modalities are not supported by this implementation.
- Biometric behavior requires device or emulator testing.
- AndroidX BiometricPrompt migration remains future work.

## Platform limitations

- The project retains Android Support Library imports.
- Compile SDK 30 and target SDK 28 are conservative legacy values.
- The Android Gradle Plugin and Gradle versions are older than current Android tooling.
- A complete Gradle wrapper JAR and wrapper properties are not included.
- Current CI does not provision an emulator for instrumentation tests.

## Product limitations

- The library provides a PIN-entry activity and keypad UI, not a complete device lock-screen replacement.
- It does not provide account recovery or PIN reset workflows.
- It does not synchronize credentials across devices.
- It does not provide remote session management.
- It does not provide a production-ready authorization architecture.

## Maintenance direction

Priorities for a future major version are AndroidX migration, BiometricPrompt integration, a complete Gradle wrapper, stronger credential policy options, persistent attempt throttling, and dedicated device-level test coverage.
