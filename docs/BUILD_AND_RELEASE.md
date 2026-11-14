# Build and release guide

This guide documents the pinned toolchain, Android SDK setup, build commands, CI tasks, and release checks for the two-module project.

## 1. Required toolchain

- JDK 11
- Gradle 6.7.1
- Android Gradle Plugin 4.2.2
- Android SDK Platform 30
- Android SDK Build Tools 30.0.3
- Android Support Library 28.0.0
- ConstraintLayout 1.1.3

Upgrade Gradle and the Android Gradle Plugin together. Do not independently move one version without checking the official compatibility matrix.

## 2. SDK installation

Install Android command-line tools and accept the SDK licenses. Install the required packages:

```bash
sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"
```

Create `local.properties` in the repository root:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

The path must point to the Android SDK directory on the current machine. Do not commit this file.

## 3. Build commands

Clean generated output:

```bash
gradle clean
```

Build the library and sample debug APK:

```bash
gradle :lockscreen:assembleDebug
gradle :sample:assembleDebug
```

Build release variants:

```bash
gradle :lockscreen:assembleRelease
gradle :sample:assembleRelease
```

Run JVM unit tests:

```bash
gradle :lockscreen:testDebugUnitTest
gradle :sample:testDebugUnitTest
```

Run lint:

```bash
gradle :lockscreen:lintDebug
gradle :sample:lintDebug
```

## 4. CI expectations

The Android CI workflow installs JDK 11, Android SDK Platform 30, Build Tools 30.0.3, and Gradle 6.7.1. It then runs clean, both module unit-test tasks, both debug assemble tasks, and lint for both modules.

A successful JVM test job does not prove fingerprint hardware works. Device-specific behavior requires emulator or physical-device instrumentation.

## 5. Versioning

The module version is configured in each module's `defaultConfig`. Update the library and sample version together when preparing a release. Keep the package namespace stable unless a breaking migration is explicitly planned.

Before releasing:

- Update the changelog.
- Verify the public activity factory methods and result codes.
- Run unit tests and lint.
- Build debug and release variants.
- Test the sample on an emulator.
- Test fingerprint behavior on supported physical hardware.
- Inspect manifest merge output.
- Verify the AAR/APK contents.
- Include the root MIT license and all third-party notices.
- Confirm that no publishing credentials are committed.

## 6. Removed publishing configuration

The original build included the obsolete Bintray plugin and read publishing credentials from `local.properties`. Bintray is retired, and the old configuration prevented normal builds when local credentials were absent. The plugin and credential loading were removed.

Do not restore publishing credentials in source control. If artifact publishing is required, choose a supported repository and use protected CI secrets with a separate release workflow.

## 7. Reproducibility and wrapper status

The repository contains Gradle launch scripts but does not contain a complete wrapper JAR and wrapper properties. CI installs the pinned Gradle distribution directly. A future change should add a complete wrapper so contributors can use `./gradlew` without a separate Gradle installation.
