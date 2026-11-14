# API reference

This reference documents the current public entry points. The package namespace is retained from the original project to avoid breaking existing consumers.

## 1. EnterPinActivity

Fully qualified name:

```java
com.amirarcane.lockscreen.activity.EnterPinActivity
```

The activity handles PIN setup, PIN verification, and optional platform fingerprint authentication.

### Constants

| Constant | Purpose |
| --- | --- |
| `RESULT_BACK_PRESSED` | Custom result code returned after Back |
| `EXTRA_SET_PIN` | Boolean Intent extra requesting setup mode |
| `EXTRA_FONT_TEXT` | Asset path for title and helper text typeface |
| `EXTRA_FONT_NUM` | Asset path for numeric keypad typeface |

### Factory: setup or verification

`getIntent(Context context, boolean setPin)` creates an activity Intent.

- `true` requests PIN setup.
- `false` requests verification.
- If verification is requested but no credential exists, the activity switches to setup mode.

```java
Intent intent = EnterPinActivity.getIntent(this, false);
startActivityForResult(intent, 1001);
```

### Factory: fonts in verification mode

`getIntent(Context context, String fontText, String fontNum)` creates a verification Intent with two optional asset paths. The paths are relative to the host application's assets directory.

```java
Intent intent = EnterPinActivity.getIntent(
    this,
    "font/title.ttf",
    "font/digits.ttf"
);
```

### Factory: setup mode and fonts

`getIntent(Context context, boolean setPin, String fontText, String fontNum)` combines the setup flag with the optional typeface paths.

```java
Intent intent = EnterPinActivity.getIntent(
    this,
    true,
    "font/title.ttf",
    "font/digits.ttf"
);
```

### Result handling

Only `Activity.RESULT_OK` represents successful completion. `RESULT_BACK_PRESSED` means the user left the activity through Back. Other result codes must be handled as cancellation or failure, not success.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
        continueProtectedAction();
    } else if (resultCode == EnterPinActivity.RESULT_BACK_PRESSED) {
        showCancelledState();
    }
}
```

## 2. PinLockView

Fully qualified name:

```java
com.amirarcane.lockscreen.andrognito.pinlockview.PinLockView
```

The view extends RecyclerView and owns the current digit buffer.

### Listener and state methods

| Method | Behavior |
| --- | --- |
| `setPinLockListener(PinLockListener)` | Registers digit-entry callbacks |
| `getPinLength()` | Returns the configured PIN length |
| `setPinLength(int)` | Sets the required number of digits, minimum one |
| `resetPinLockView()` | Clears input and resets indicator state |
| `attachIndicatorDots(IndicatorDots)` | Connects an indicator view |
| `isIndicatorDotsAttached()` | Reports whether an indicator is connected |

### Appearance methods

| Method | Units / behavior |
| --- | --- |
| `setTypeFace(Typeface)` | Updates numeric button typeface |
| `setTextColor(int)` | Android color integer |
| `setTextSize(int)` | Pixels |
| `setButtonSize(int)` | Pixels, square numeric button |
| `setButtonBackgroundDrawable(Drawable)` | Numeric button background |
| `setDeleteButtonDrawable(Drawable)` | Delete icon |
| `setDeleteButtonWidthSize(int)` | Pixels |
| `setDeleteButtonHeightSize(int)` | Pixels |
| `setDeleteButtonPressedColor(int)` | Android color integer |
| `setShowDeleteButton(boolean)` | Visibility of delete control |

Convert density-independent values to pixels before calling pixel-based methods.

### Custom digit order

`setCustomKeySet(int[])` accepts ten values. Every decimal digit must occur once.

```java
keypad.setCustomKeySet(new int[] { 2, 4, 6, 8, 0, 1, 3, 5, 7, 9 });
```

The method clones the caller's array and rejects null, wrong-length, duplicate, or out-of-range arrays with `IllegalArgumentException`.

### Randomized layout

`enableLayoutShuffling()` uses SecureRandom and works on a copy of the default order. The static default array is not mutated.

## 3. PinLockListener

The listener has three methods:

- `onComplete(String pin)` is called when the configured PIN length is reached.
- `onEmpty()` is called when the user deletes all digits or clears the keypad.
- `onPinChange(int pinLength, String intermediatePin)` is called when a partial entry changes.

The intermediate PIN is sensitive. Do not log it, persist it, send it to analytics, or attach it to a crash report.

## 4. IndicatorDots

Fully qualified name:

```java
com.amirarcane.lockscreen.andrognito.pinlockview.IndicatorDots
```

Attach the view to the keypad using `attachIndicatorDots`. It can display fixed, filled, or animated indicators. The activity currently configures animated fill.

## 5. FingerPrintListener

The listener receives success, mismatch, fatal error, and help callbacks from the platform fingerprint flow. Callbacks are delivered by the platform authentication handler. Consumers should not retain activity references beyond the activity lifecycle.

## 6. FingerprintHandler

This class wraps FingerprintManager.AuthenticationCallback.

- Set the listener before starting authentication.
- Start authentication only while the activity is resumed.
- Cancel authentication when the activity pauses.
- Do not assume the device has biometric hardware.
- Do not assume fingerprint enrollment is configured.
- Prefer AndroidX Biometric for new applications.

## 7. XML attributes

Custom keypad attributes are declared in `lockscreen/src/main/res/values/attrs.xml`. When changing an attribute, update all of the following:

1. The styleable declaration.
2. The default value resource, if one exists.
3. The Java attribute reader.
4. The runtime setter, if it is mutable.
5. The API documentation and sample.

## 8. API stability

The package names, Intent extras, result codes, and public listener interfaces are part of the integration surface. Changes to them should be treated as breaking changes and accompanied by migration documentation.
