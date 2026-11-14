# Testing

The test strategy separates platform-independent credential logic from Android-specific UI and biometric behavior.

## 1. Local unit tests

The library unit tests run on the JVM and cover:

- Four-digit PIN validation.
- Rejection of null, empty, too-short, too-long, and non-decimal input.
- Random salt generation and expected salt length.
- PBKDF2 verifier creation.
- Correct PIN acceptance.
- Incorrect PIN rejection.
- Different verifiers for the same PIN when salts differ.
- Malformed stored verifier rejection.
- Legacy SHA-256 helper availability for migration.

Run:

```bash
gradle :lockscreen:testDebugUnitTest
```

The PBKDF2 tests deliberately exercise the real derivation function. Do not replace them with a trivial mock that skips the cryptographic operation.

## 2. Sample unit tests

Run:

```bash
gradle :sample:testDebugUnitTest
```

The sample currently has minimal application-level logic. Add tests when sample behavior becomes more complex rather than creating tests that merely assert constants or arithmetic.

## 3. Build verification

Build both modules:

```bash
gradle clean
gradle :lockscreen:assembleDebug
gradle :sample:assembleDebug
```

This catches Java compilation, resource linking, dependency resolution, manifest merging, and packaging failures.

## 4. Lint

Run:

```bash
gradle :lockscreen:lintDebug
gradle :sample:lintDebug
```

Review warnings about deprecated APIs, target SDK behavior, accessibility, manifest declarations, and resource compatibility. Do not silence a warning without understanding why it is safe.

## 5. Instrumentation tests

Instrumented tests require an emulator or physical device:

```bash
gradle :lockscreen:connectedDebugAndroidTest
gradle :sample:connectedDebugAndroidTest
```

The current CI workflow does not provision an emulator. Therefore it should not claim that device instrumentation or fingerprint hardware was tested.

## 6. Manual test plan

### PIN setup

1. Launch setup mode.
2. Enter four digits.
3. Confirm the same four digits.
4. Confirm the activity returns `RESULT_OK`.
5. Inspect preferences in a debug test environment and confirm raw PIN is absent.

### PIN mismatch

1. Enter one PIN.
2. Enter a different confirmation PIN.
3. Confirm the keypad resets.
4. Confirm the first attempt is cleared.
5. Confirm no credential was persisted.

### PIN verification

1. Configure a PIN.
2. Launch verification mode.
3. Enter the correct PIN and confirm success.
4. Repeat with an incorrect PIN and confirm failure.
5. Confirm the activity remains open after a mismatch.

### Legacy migration

1. Seed a test preference with the old SHA-256 value.
2. Enter the correct PIN.
3. Confirm the new salt and PBKDF2 verifier are written.
4. Confirm the legacy key is removed.
5. Repeat with an incorrect PIN and confirm the legacy value remains unchanged.

### Keypad

1. Enter and delete a digit.
2. Long-press delete and confirm all digits clear.
3. Hide and show the delete button.
4. Apply a custom key order.
5. Pass duplicate and invalid digits and confirm validation fails.
6. Enable randomized layout more than once and confirm the default order is not mutated.

### Fingerprint

1. Test a device with no fingerprint hardware.
2. Test hardware with no enrolled fingerprint.
3. Test a device without secure system lock.
4. Test successful and failed authentication on a supported device.
5. Background the activity during authentication and confirm cancellation.
6. Return to the activity and confirm a new request can be started.

## 7. Test evidence

Record the commit SHA, Gradle version, JDK version, Android SDK platform, task name, and full failure output when reporting test results. A workflow that is queued or in progress is not a passing test.
