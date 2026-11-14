# Lock Screen

A reusable Android PIN-entry screen with a customizable numeric keypad, PIN confirmation flow, optional fingerprint authentication, animated input indicators, and a sample application.

The repository contains two Gradle modules:

- **lockscreen** is the Android library. It provides `EnterPinActivity`, `PinLockView`, `IndicatorDots`, and the fingerprint integration.
- **sample** is a demonstration app showing setup, verification, and typeface customization.

> **Security boundary:** This library provides a PIN-entry UI and local verification flow. It does not replace Android's device lock screen, encrypt your application's database or files, or automatically protect every screen. The host application must check the result and enforce access control before exposing protected data.

## Contents

- [Features](#features)
- [Repository layout](#repository-layout)
- [Requirements](#requirements)
- [Build the project](#build-the-project)
- [Run the sample](#run-the-sample)
- [Integrate the library](#integrate-the-library)
- [PIN setup and verification](#pin-setup-and-verification)
- [Fingerprint authentication](#fingerprint-authentication)
- [Keypad customization](#keypad-customization)
- [Typefaces](#typefaces)
- [PIN storage and migration](#pin-storage-and-migration)
- [Compatibility](#compatibility)
- [Testing](#testing)
- [Security limitations](#security-limitations)
- [Documentation](#documentation)
- [Licenses and third-party notices](#licenses-and-third-party-notices)

## Features

### PIN entry
- Four-digit PIN setup with two-entry confirmation.
- PIN verification against a locally stored credential verifier.
- Delete-last-digit and long-press-to-clear keypad actions.
- Custom digit order and randomized keypad layout.
- Indicator dots for entered digit count.
- Configurable keypad colors, dimensions, delete icon, and typefaces.

### Fingerprint
- Optional Android FingerprintManager integration on API 23 and later.
- Checks hardware, enrolled fingerprints, app permission, and secure device lock.
- Cancels an authentication request when the activity pauses.
- Keeps PIN entry available when fingerprint authentication is unsupported or unavailable.

### Engineering
- Separate library and sample modules.
- Salted PBKDF2 PIN verifier for new credentials.
- One-time upgrade path for legacy SHA-256 PIN credentials.
- JVM tests for PIN validation and credential derivation.
- Android build, test, lint, CodeQL, and Markdown workflows.

## Repository layout

```text
.
├── .github/workflows/
├── docs/
├── licenses/
├── lockscreen/
│   └── src/main/java/com/amirarcane/lockscreen/
│       ├── activity/
│       ├── andrognito/pinlockview/
│       ├── fingerprint/
│       └── util/
├── sample/
│   └── src/main/
├── build.gradle
├── settings.gradle
└── README.md
```

The package namespace remains `com.amirarcane.lockscreen` to preserve the existing public API and sample integration.

## Requirements

| Requirement | Configuration |
| --- | --- |
| Language | Java |
| Minimum Android SDK | API 17 |
| Compile SDK | API 30 |
| Target SDK | API 28 |
| Android Gradle Plugin | 4.2.2 |
| Gradle | 6.7.1 |
| JDK | 11 |
| Support Library | 28.0.0 |
| ConstraintLayout | 1.1.3 |

This is a compatibility-focused maintenance baseline. The Android Support Library and platform FingerprintManager API are deprecated. A future major release should migrate to AndroidX and BiometricPrompt with a documented breaking-change plan.

## Build the project

Install JDK 11, Gradle 6.7.1, Android SDK Platform 30, and Build Tools 30.0.3.

Clone the repository:

```bash
git clone https://github.com/Brajesh31/lock-screen.git
cd lock-screen
```

Create `local.properties` in the repository root with your local Android SDK path:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

This machine-specific file is ignored by Git.

Build the modules:

```bash
gradle clean
gradle :lockscreen:assembleDebug
gradle :sample:assembleDebug
```

Run unit tests and lint:

```bash
gradle :lockscreen:testDebugUnitTest
gradle :sample:testDebugUnitTest
gradle :lockscreen:lintDebug
gradle :sample:lintDebug
```

The repository currently has Gradle launch scripts but no complete wrapper JAR or wrapper properties. CI installs the pinned Gradle version directly. See [Build and release](docs/BUILD_AND_RELEASE.md).

## Run the sample

1. Open the repository root in Android Studio.
2. Complete Gradle sync.
3. Select the `sample` run configuration.
4. Choose an emulator or physical device.
5. Run the application.

The sample screen demonstrates verification mode, PIN setup, custom fonts, and setup with custom fonts. The bundled font files are examples. Check their individual redistribution terms before reusing them in another application.

## Integrate the library

Include the library module in the host application's `settings.gradle`:

```groovy
include ':lockscreen'
project(':lockscreen').projectDir = new File('../lock-screen/lockscreen')
```

Add the dependency:

```groovy
dependencies {
    implementation project(':lockscreen')
}
```

Declare the activity in the host manifest:

```xml
<activity
    android:name="com.amirarcane.lockscreen.activity.EnterPinActivity"
    android:exported="false"
    android:theme="@style/Theme.AppCompat.NoActionBar" />
```

Keep the activity non-exported unless there is a reviewed requirement to expose it to other apps.

### Start verification

```java
Intent intent = EnterPinActivity.getIntent(this, false);
startActivityForResult(intent, 1001);
```

If no PIN has been configured, the activity automatically moves into setup mode.

### Start setup

```java
Intent intent = EnterPinActivity.getIntent(this, true);
startActivityForResult(intent, 1002);
```

The user must enter the same four digits twice. A mismatch resets the entry and asks the user to try again.

### Handle the result

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
        // Continue only with the protected action for this request.
    } else if (resultCode == EnterPinActivity.RESULT_BACK_PRESSED) {
        // The user backed out of the PIN screen.
    }
}
```

Only `Activity.RESULT_OK` represents success. Do not treat every other result as authenticated.

## PIN setup and verification

The activity uses a four-digit PIN by default. During setup, the first entry is held in memory and compared with the second entry. Only matching entries are persisted.

During verification, the activity derives a verifier using the stored salt and compares it with the stored verifier. Incorrect attempts leave the activity open and reset the keypad. The PIN itself is not stored in preferences.

## Fingerprint authentication

Fingerprint authentication is optional and only prepared on API 23 or later. The activity checks for hardware, enrolled fingerprints, the declared permission, and a secure device lock screen.

This implementation uses the legacy platform `FingerprintManager`, which is deprecated on modern Android. It does not claim support for face unlock or every biometric modality. New host applications should use AndroidX Biometric and BiometricPrompt.

PIN entry remains available when fingerprint authentication is not available.

## Keypad customization

`PinLockView` supports common visual and layout changes:

```java
PinLockView keypad = findViewById(R.id.pinlockView);
keypad.setPinLength(4);
keypad.setTextColor(Color.WHITE);
keypad.setTextSize(24);
keypad.setButtonSize(64);
keypad.setShowDeleteButton(true);
keypad.enableLayoutShuffling();
```

Provide a custom order with each digit exactly once:

```java
keypad.setCustomKeySet(new int[] { 8, 1, 4, 0, 9, 2, 7, 5, 3, 6 });
```

Invalid arrays, duplicate digits, missing digits, and out-of-range values are rejected. Dimension setters use pixels, so convert dp values before passing them.

## Typefaces

Font paths are relative to the host application's assets directory:

```java
Intent intent = EnterPinActivity.getIntent(
    this,
    "font/title-font.ttf",
    "font/number-font.ttf"
);
startActivityForResult(intent, 1001);
```

The combined factory method accepts the setup flag followed by the two font paths. Invalid font paths should not be relied on; the activity retains the default typeface when a custom font cannot be loaded.

## PIN storage and migration

New credentials use PBKDF2WithHmacSHA1 with a random 16-byte salt, 120,000 iterations, and a 256-bit output. The salt and derived verifier are stored in private SharedPreferences. Each new PIN receives a fresh salt.

Older installations may contain an unsalted SHA-256 digest. Because a four-digit PIN has only 10,000 combinations, that format is too fast to brute-force. The library checks the legacy value only when necessary and upgrades it to PBKDF2 after a successful PIN entry.

This migration does not recover a PIN if application data has been cleared. The user must configure a new PIN after data loss.

## Compatibility

- API 17 through API 22: keypad and PIN verification work; fingerprint controls are hidden.
- API 23 and later: fingerprint is offered only when all platform checks pass.
- Android Support Library is retained for source compatibility.
- FingerprintManager is deprecated and should be replaced in a future major release.

See [Compatibility](docs/COMPATIBILITY.md) for migration guidance.

## Testing

Run:

```bash
gradle :lockscreen:testDebugUnitTest
gradle :sample:testDebugUnitTest
gradle :lockscreen:assembleDebug
gradle :sample:assembleDebug
```

The library tests cover PIN format validation, salt generation, PBKDF2 verifier generation and verification, invalid stored values, and the legacy SHA-256 helper. Instrumented tests require an emulator or physical device. See [Testing](docs/TESTING.md).

## Security limitations

This library is a UI and local credential-verification component, not a certified authentication product.

- Four-digit PINs have low entropy. PBKDF2 increases offline guessing cost but does not make the PIN strong.
- Persistent failed-attempt lockout is not implemented.
- Private SharedPreferences may be extracted on a rooted or compromised device.
- The host app must independently enforce authorization after `RESULT_OK`.
- Fingerprint templates are managed by Android; the app receives callbacks, not biometric images.
- The library does not encrypt host application data or replace the device lock screen.

Read [Security](docs/SECURITY.md) before using the component to gate sensitive information.

## Documentation

| Document | Scope |
| --- | --- |
| [Architecture](docs/ARCHITECTURE.md) | Modules, activity flow, keypad and fingerprint lifecycle |
| [API reference](docs/API_REFERENCE.md) | Public factory methods, result handling, keypad API |
| [Build and release](docs/BUILD_AND_RELEASE.md) | Toolchain, Gradle tasks, release checklist |
| [Compatibility](docs/COMPATIBILITY.md) | SDK baseline and future migration |
| [Maintenance](docs/MAINTENANCE.md) | Dependencies, source hygiene, upkeep |
| [Security](docs/SECURITY.md) | PIN storage, threat model, host responsibilities |
| [Testing](docs/TESTING.md) | Unit, instrumentation, build and lint |
| [Troubleshooting](docs/TROUBLESHOOTING.md) | Build, integration and runtime problems |
| [Third-party notices](THIRD_PARTY_NOTICES.md) | Upstream source and component licenses |

## Licenses and third-party notices

This repository includes code derived from the original Lock Screen project and the PinLockView component. Their copyright notices and license terms must remain intact. See [LICENSE](LICENSE), [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md), and [licenses/Apache-2.0.txt](licenses/Apache-2.0.txt).

Maintainer: **Brajesh Kumar**

Last updated: 2026-11-14 UTC
