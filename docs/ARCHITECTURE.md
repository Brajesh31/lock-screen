# Architecture

This guide explains the library modules, activity lifecycle, PIN state transitions, keypad internals, persistence boundary, and fingerprint callback ownership. It is intended for maintainers who need to change behavior without breaking the existing Java API.

## 1. Modules

### lockscreen

The library module contains the reusable activity, keypad, fingerprint adapter, utility methods, layouts, drawable resources, and localized strings.

| Package | Responsibility |
| --- | --- |
| `activity` | PIN setup, PIN verification, result codes, preferences, and fingerprint initialization |
| `andrognito.pinlockview` | Numeric keypad, adapter, indicator dots, item spacing, and customization state |
| `fingerprint` | FingerprintManager callback wrapper and listener contract |
| `util` | PIN verifier derivation, input validation, and animation helpers |
| `res/layout` | PIN screen and individual keypad item layouts |
| `res/values` | Strings, dimensions, colors, styles, and custom view attributes |
| `res/drawable*` | Button states, fingerprint icons, and indicator backgrounds |
| `res/animator` | Fingerprint state transition animations |

### sample

The sample module demonstrates how a host application launches the activity and handles its result. It is deliberately small. It does not implement a complete protected-data architecture or a production account system.

## 2. Activity startup sequence

`EnterPinActivity` performs the following work in `onCreate`:

1. Inflates `activity_enterpin.xml`.
2. Resolves the title, attempts message, fingerprint image, fingerprint label, keypad, and indicator dots.
3. Loads animated fingerprint drawables only on API 23 or later.
4. Reads the setup flag from the incoming Intent.
5. Checks whether a stored PIN verifier exists when verification mode was requested.
6. Switches to setup mode when requested or when no stored credential exists.
7. Prepares fingerprint authentication only on API 23 or later.
8. Attaches the PinLockListener.
9. Configures the default PIN length and indicator mode.
10. Applies optional typefaces from the host application's assets.

The keypad is initialized on every supported API level. Fingerprint support is an optional enhancement and must never be a prerequisite for PIN entry.

## 3. PIN setup state machine

The setup flow has two input states:

- **First entry:** The four entered digits are retained in the activity's `mFirstPin` field.
- **Confirmation entry:** The next four digits are compared with the first entry.

When both entries match, the activity creates a random salt, derives a PBKDF2 verifier, persists the verifier and salt, returns `RESULT_OK`, and finishes.

When they do not match, the activity resets the keypad, clears the first entry, displays a mismatch message, and allows the user to start over.

The first PIN value is transient. It must not be logged, persisted before confirmation, or included in crash diagnostics.

## 4. PIN verification state machine

The verification flow reads the current credential format from private SharedPreferences.

1. If both the current verifier and salt are present, the entered PIN is processed with PBKDF2.
2. The derived bytes are compared with the stored verifier using `MessageDigest.isEqual`.
3. A successful comparison returns `RESULT_OK`.
4. A failed comparison displays an error and clears the keypad.
5. If only the legacy SHA-256 value is present, the library checks it once.
6. A successful legacy check triggers an immediate PBKDF2 upgrade and removes the old preference.

Malformed current verifier values fail closed. They must not be treated as a successful credential.

## 5. PIN keypad component

`PinLockView` extends RecyclerView. It owns the current input buffer and delegates individual keypad cell rendering to `PinLockAdapter`.

The adapter has twelve positions. Ten positions represent digits, one position is intentionally blank to preserve the familiar telephone keypad layout, and the final position is reserved for delete.

The adapter's delete control must always register click and long-click listeners when its ViewHolder is created. RecyclerView can create that holder before any digit is entered. Visibility is controlled later when the current PIN length changes.

The view reports state changes through `PinLockListener`. The listener's intermediate PIN parameter is sensitive and must never be written to logs or analytics.

## 6. Keypad customization

`CustomizationOptionsBundle` stores appearance values shared between the view and adapter. The view exposes methods for changing:

- Text color and text size.
- Numeric button dimensions.
- Numeric button background.
- Delete icon and dimensions.
- Delete icon tint.
- PIN length.
- Custom digit order.
- Randomized layout.

The digit order must contain each value from zero through nine exactly once. The view validates custom arrays before passing them to the adapter.

The randomized layout uses a cloned default array. Shuffling must never mutate a static shared array because a later keypad instance could otherwise inherit an unexpected default order.

## 7. Indicator dots

`IndicatorDots` is a LinearLayout that reflects the number of digits entered. It supports fixed dots, filled dots, and animated filled dots.

The keypad updates the indicator after every digit, delete operation, long-press clear, and reset. When the PIN length changes, the indicator removes and recreates its children to match the new length.

## 8. Fingerprint lifecycle

The platform FingerprintManager is available from API 23. Before preparing it, the activity checks service availability, sensor hardware, enrollment, permission, and secure lock-screen state.

The activity creates the Keystore key and cipher, creates a CryptoObject, and registers its listener before starting the authentication request. The request begins in `onResume` and is cancelled in `onPause`.

The FingerprintHandler tracks whether a request is active. It ignores callbacks after cancellation so a delayed callback does not attempt to update a paused or stopped activity.

Fingerprint authentication is not required for PIN verification. If any setup step fails, fingerprint controls are hidden and the PIN keypad remains usable.

## 9. Result contract

The activity returns `Activity.RESULT_OK` when PIN setup completes, the PIN matches, or the fingerprint callback reports success. It returns `RESULT_BACK_PRESSED` when the user presses Back.

The library cannot know which host-application resource is protected. The host must check the request code and exact result before continuing with the intended protected action.

## 10. Persistence boundary

| Value | Lifetime | Storage |
| --- | --- | --- |
| First PIN during setup | Activity lifetime | Memory |
| Current keypad input | View lifetime | Memory |
| PBKDF2 verifier | Across app launches | Private SharedPreferences |
| Random salt | Across app launches | Private SharedPreferences |
| Fingerprint callback and cipher | Authentication session | Memory / Android Keystore |

The library does not store raw PIN values in persistent storage. Host applications must not introduce a parallel raw-PIN storage path.

## 11. Future architecture work

The next major architecture improvement should migrate the support-library UI to AndroidX, replace FingerprintManager with AndroidX BiometricPrompt, add a complete Gradle wrapper, and separate credential storage behind a testable interface. Such a migration changes dependencies and potentially public integration behavior, so it should be released with a migration guide.
