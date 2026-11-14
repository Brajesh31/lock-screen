package com.amirarcane.lockscreen;

import com.amirarcane.lockscreen.util.Utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Focused tests for PIN validation and credential derivation.
 *
 * These tests exercise the platform-independent security helpers without
 * requiring a device, emulator, Android Keystore, or biometric hardware.
 */
public class ExampleUnitTest {

    @Test
    public void acceptsOnlyFourDecimalDigits() {
        assertTrue(Utils.isValidPin("0000"));
        assertTrue(Utils.isValidPin("1234"));
        assertFalse(Utils.isValidPin(null));
        assertFalse(Utils.isValidPin(""));
        assertFalse(Utils.isValidPin("123"));
        assertFalse(Utils.isValidPin("12345"));
        assertFalse(Utils.isValidPin("12a4"));
        assertFalse(Utils.isValidPin("１２３４"));
    }

    @Test
    public void generatedSaltHasExpectedLengthAndIsNotConstant() {
        byte[] first = Utils.generateSalt();
        byte[] second = Utils.generateSalt();

        assertEquals(16, first.length);
        assertEquals(16, second.length);
        assertFalse(java.util.Arrays.equals(first, second));
    }

    @Test
    public void saltedVerifierAcceptsCorrectPin() {
        byte[] salt = Utils.generateSalt();
        String verifier = Utils.hashPin("0427", salt);

        assertTrue(Utils.verifyPin("0427", Utils.toHex(salt), verifier));
    }

    @Test
    public void saltedVerifierRejectsIncorrectPin() {
        byte[] salt = Utils.generateSalt();
        String verifier = Utils.hashPin("0427", salt);

        assertFalse(Utils.verifyPin("0428", Utils.toHex(salt), verifier));
    }

    @Test
    public void differentSaltsProduceDifferentVerifiersForSamePin() {
        String first = Utils.hashPin("1234", Utils.generateSalt());
        String second = Utils.hashPin("1234", Utils.generateSalt());

        assertNotEquals(first, second);
    }

    @Test
    public void rejectsMalformedStoredVerifier() {
        assertFalse(Utils.verifyPin("1234", "not-hex", "also-not-hex"));
        assertFalse(Utils.verifyPin("1234", "", ""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void refusesToHashInvalidPin() {
        Utils.hashPin("12", Utils.generateSalt());
    }

    @Test
    public void legacySha256RemainsAvailableForCredentialMigration() {
        assertEquals(64, Utils.sha256("1234").length());
    }
}
