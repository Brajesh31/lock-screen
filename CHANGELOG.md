# Changelog

Changes that affect consumers and maintainers are recorded here.

## Unreleased

### Security
- Replaced new PIN storage based on unsalted SHA-256 with a random-salt PBKDF2 verifier.
- Added a one-time migration path for existing SHA-256 credentials after successful PIN verification.
- Removed intermediate PIN values from debug logging.
- Added validation for PIN format and custom keypad digit arrays.

### Android behavior
- Initialized the PIN keypad on API 17 through API 22 instead of leaving the activity blank below API 23.
- Made fingerprint authentication optional and lifecycle-aware.
- Added cancellation when the activity pauses and ignored callbacks after cancellation.
- Improved fingerprint availability checks and failure fallback.

### Keypad
- Registered delete-button listeners when ViewHolders are created, including before the first digit is entered.
- Corrected delete icon height updates.
- Corrected delete-button tint handling.
- Refreshed keypad items after typeface changes.
- Prevented randomized layouts from mutating the shared default digit array.

### Build and documentation
- Replaced Android Gradle Plugin 2.3.3 with 4.2.2.
- Removed obsolete Bintray publishing configuration and JCenter repositories.
- Updated support-library dependencies and pinned a compatible Gradle/JDK baseline.
- Added detailed project, API, architecture, security, compatibility, testing, and maintenance documentation.
- Added Android CI, CodeQL, Markdown lint, and Dependabot configuration.
