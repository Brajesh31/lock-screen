# Support

Before opening an issue, review the README and the troubleshooting guide.

## Include in a support request

- Android version and API level.
- Device or emulator model.
- JDK version.
- Gradle version.
- Android Gradle Plugin version.
- Build variant and exact task.
- Repository commit SHA.
- Full error message or sanitized stack trace.
- Steps to reproduce the problem.
- Whether the issue affects the sample app or a consuming application.

## Do not include

- PINs or partial PINs.
- Signing keys or passwords.
- API tokens.
- Personal or confidential data.
- Unredacted logs containing account information.

## Build questions

The current project baseline uses JDK 11, Gradle 6.7.1, Android Gradle Plugin 4.2.2, SDK Platform 30, and Build Tools 30.0.3. Use those versions when reproducing build issues.

## Fingerprint questions

Fingerprint behavior depends on device hardware, enrollment, system lock-screen configuration, and Android API level. Include these details when reporting biometric issues. The sample cannot emulate every physical sensor behavior.

## Documentation

See [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md), [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md), and [docs/TESTING.md](docs/TESTING.md) before opening an issue.
