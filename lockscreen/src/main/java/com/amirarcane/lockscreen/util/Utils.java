package com.amirarcane.lockscreen.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Security and encoding helpers used by the lock-screen flow.
 *
 * PINs have a very small search space. A plain SHA-256 digest is not an
 * appropriate password-storage format because an attacker can test all
 * possible four-digit values quickly. New PINs are therefore stored using
 * PBKDF2 with a fresh random salt and a deliberately expensive iteration
 * count. The legacy SHA-256 method remains only to migrate existing installs.
 */
public final class Utils {

    private static final int SALT_LENGTH_BYTES = 16;
    private static final int PBKDF2_ITERATIONS = 120000;
    private static final int HASH_LENGTH_BITS = 256;

    private Utils() {
        throw new AssertionError("No instances");
    }

    /** Generate a cryptographically random salt for one PIN credential. */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Derive a salted PIN verifier.
     *
     * @param pin four-digit PIN entered by the user
     * @param salt random salt stored alongside the verifier
     * @return hexadecimal PBKDF2 verifier
     */
    public static String hashPin(String pin, byte[] salt) {
        if (!isValidPin(pin)) {
            throw new IllegalArgumentException("PIN must contain exactly four digits.");
        }
        if (salt == null || salt.length < SALT_LENGTH_BYTES) {
            throw new IllegalArgumentException("A salt of at least 16 bytes is required.");
        }

        char[] password = pin.toCharArray();
        PBEKeySpec specification = new PBEKeySpec(
                password, salt, PBKDF2_ITERATIONS, HASH_LENGTH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return toHex(factory.generateSecret(specification).getEncoded());
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to derive the PIN verifier.", exception);
        } finally {
            specification.clearPassword();
            Arrays.fill(password, '\0');
        }
    }

    /**
     * Verify a PIN using the stored salt and verifier. MessageDigest.isEqual
     * performs the final byte comparison without an early-exit string compare.
     */
    public static boolean verifyPin(String pin, String saltHex, String expectedHashHex) {
        if (!isValidPin(pin) || saltHex == null || expectedHashHex == null) {
            return false;
        }
        try {
            byte[] salt = fromHex(saltHex);
            byte[] expected = fromHex(expectedHashHex);
            byte[] actual = fromHex(hashPin(pin, salt));
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    /** Return true only for a four-character decimal PIN. */
    public static boolean isValidPin(String pin) {
        if (pin == null || pin.length() != 4) {
            return false;
        }
        for (int index = 0; index < pin.length(); index++) {
            if (pin.charAt(index) < '0' || pin.charAt(index) > '9') {
                return false;
            }
        }
        return true;
    }

    /** Encode bytes as lowercase hexadecimal text for SharedPreferences. */
    public static String toHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format(java.util.Locale.US, "%02x", value & 0xff));
        }
        return result.toString();
    }

    private static byte[] fromHex(String value) {
        if (value.length() == 0 || value.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hexadecimal value.");
        }
        byte[] result = new byte[value.length() / 2];
        for (int index = 0; index < value.length(); index += 2) {
            int high = Character.digit(value.charAt(index), 16);
            int low = Character.digit(value.charAt(index + 1), 16);
            if (high < 0 || low < 0) {
                throw new IllegalArgumentException("Invalid hexadecimal value.");
            }
            result[index / 2] = (byte) ((high << 4) + low);
        }
        return result;
    }

    /**
     * Legacy verifier used only when upgrading credentials written by older
     * releases. Do not use this method for newly created PINs.
     */
    @Deprecated
    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return toHex(digest.digest(value.getBytes("UTF-8")));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }
}
