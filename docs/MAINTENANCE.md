# Maintenance

This document defines the routine maintenance expected for the library and sample application.

## 1. Dependency maintenance

The project uses Android Gradle Plugin 4.2.2, Gradle 6.7.1, Support Library 28.0.0, and ConstraintLayout 1.1.3. These versions are intentionally pinned as a compatible baseline.

Review dependency update proposals in a controlled branch or commit sequence. Upgrade the Android Gradle Plugin and Gradle distribution as a tested pair. Verify Android SDK compatibility and run both module builds after dependency changes.

## 2. Retired infrastructure

The original build used JCenter and the Bintray publishing plugin. Those services are obsolete for this project. Keep Google Maven and Maven Central as the dependency repositories. Do not reintroduce retired publishing plugins or hardcoded publishing credentials.

## 3. Security-sensitive changes

Changes to PIN storage, hashing parameters, legacy migration, fingerprint callbacks, or result handling require focused tests and a security review.

Never log the PIN or listener intermediate value. Keep malformed verifier behavior fail-closed. Test migration with both correct and incorrect legacy PINs.

## 4. API stability

The package namespace, Intent factory methods, result codes, and listener interfaces are public integration points. Breaking changes require a major-version plan and consumer migration notes.

## 5. Resource maintenance

When changing a custom attribute, update its XML declaration, default resource, Java reader, setter, sample usage, and API reference. Keep accessibility labels for keypad controls descriptive.

## 6. CI maintenance

The Android CI workflow should use the pinned JDK, Gradle, Android SDK, and Build Tools versions. It should build both modules, run unit tests, and run lint. CodeQL scans Java code, while Markdown lint validates documentation.

Do not report instrumentation coverage unless an emulator or physical device actually ran the test task.

## 7. Documentation maintenance

Update the README and relevant docs whenever public behavior, toolchain versions, compatibility, security controls, or build instructions change. Keep examples executable and consistent with the current Java API.

## 8. Release maintenance

Before a release, update the version, changelog, license notices, compatibility notes, and build evidence. Inspect the final AAR and APK, review merged manifests, and verify the sample app on a real device or emulator.
