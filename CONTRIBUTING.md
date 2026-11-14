# Contributing

Contributions should improve correctness, security, maintainability, accessibility, or developer experience without obscuring the original purpose of the library.

## Before changing code

1. Read the README and relevant architecture/API documentation.
2. Identify whether the change affects the library module, sample module, or both.
3. Check the public Java API and package compatibility.
4. Preserve upstream license and attribution notices in derived source files.
5. Reproduce the issue using the pinned Android build toolchain.

## Development setup

Use JDK 11, Gradle 6.7.1, Android SDK Platform 30, and Build Tools 30.0.3.

Create a local `local.properties` file with the SDK path. Do not commit this file.

## Code guidelines

- Keep Java code compatible with the configured Android Gradle Plugin.
- Prefer small methods with clear responsibilities.
- Validate public input and fail closed for malformed credential data.
- Never log raw PINs or intermediate PIN callback values.
- Keep fingerprint authentication optional and lifecycle-aware.
- Do not mutate shared static arrays when randomizing keypad layouts.
- Keep user-facing strings in Android resources.
- Preserve the existing package namespace unless a breaking migration is approved.
- Do not add credentials, signing keys, or local configuration to source control.

## Tests

For a PIN-storage change, add tests for correct input, incorrect input, malformed stored data, and legacy migration behavior. For keypad changes, test custom digit validation, randomized ordering, delete behavior, and indicator updates where possible.

Run:

```bash
gradle clean
gradle :lockscreen:testDebugUnitTest
gradle :sample:testDebugUnitTest
gradle :lockscreen:assembleDebug
gradle :sample:assembleDebug
gradle :lockscreen:lintDebug
gradle :sample:lintDebug
```

Device-specific biometric changes require an emulator or physical device. Do not claim device coverage from JVM tests.

## Documentation

Update the README and relevant docs whenever public behavior, result codes, dependencies, SDK compatibility, security controls, or build instructions change.

## Pull requests

A pull request should include:

- A clear problem statement.
- A concise explanation of the implementation.
- Tests and build tasks executed.
- Screenshots or a short recording for UI changes.
- Device/API information for platform-specific behavior.
- License and attribution notes when modifying third-party-derived code.

Keep unrelated refactoring out of focused fixes.
