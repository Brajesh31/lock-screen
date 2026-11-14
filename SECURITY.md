# Security Policy

## Scope

The maintained default branch is the current security review target. The library handles local PIN verification and optional Android fingerprint callbacks.

## Supported versions

Security fixes are applied to the default branch. Older commits and copied binaries may retain known weaknesses, including the previous unsalted SHA-256 PIN storage behavior.

## Reporting a vulnerability

Do not disclose a PIN, secret, exploit chain, personal information, or unredacted device data in a public issue. Use GitHub's private vulnerability reporting feature for this repository when available.

Include the affected commit, Android API level, device type, impact, reproduction steps, and sanitized logs.

## Security-sensitive areas

- PIN verifier generation and legacy migration.
- SharedPreferences access.
- PIN and intermediate-input logging.
- Fingerprint authentication lifecycle.
- Manifest exported-component declarations.
- Dependency and build-tool updates.

See [docs/SECURITY.md](docs/SECURITY.md) for the threat model, host-application responsibilities, and release checklist.

## Important limitation

This library is not a certified authentication product and does not replace device-level security or host-application data encryption.
