package com.amirarcane.lockscreen.fingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;

/**
 * Owns one platform fingerprint authentication request.
 *
 * The hosting Activity must cancel the request when it is no longer in the
 * foreground. Set the listener before calling startAuth so a fast callback
 * cannot arrive before the receiver has been registered.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private final Context context;
    private CancellationSignal cancellationSignal;
    private FingerPrintListener fingerPrintListener;\n    private boolean authenticationActive;

    public FingerprintHandler(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context.getApplicationContext();
    }

    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {
        if (manager == null || cryptoObject == null) {
            notifyError("Fingerprint authentication is unavailable.");
            return;
        }
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            notifyError("Fingerprint permission has not been granted.");
            return;
        }

        cancel();
        cancellationSignal = new CancellationSignal();\n        authenticationActive = true;
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void cancel() {
        authenticationActive = false;
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
        cancellationSignal = null;
    }

    public void setFingerPrintListener(FingerPrintListener listener) {
        this.fingerPrintListener = listener;
    }

    private void notifyError(CharSequence message) {
        if (authenticationActive && fingerPrintListener != null) {
            fingerPrintListener.onError(message);
        }
    }

    @Override
    public void onAuthenticationError(int errorId, CharSequence error) {
        notifyError(error);
    }

    @Override
    public void onAuthenticationFailed() {
        if (authenticationActive && fingerPrintListener != null) {
            fingerPrintListener.onFailed();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpId, CharSequence help) {
        if (authenticationActive && fingerPrintListener != null) {
            fingerPrintListener.onHelp(help);
        }
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        if (authenticationActive && fingerPrintListener != null) {
            fingerPrintListener.onSuccess();
        }
    }
}
