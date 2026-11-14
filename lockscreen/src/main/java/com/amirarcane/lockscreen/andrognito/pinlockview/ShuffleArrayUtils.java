package com.amirarcane.lockscreen.andrognito.pinlockview;

import java.security.SecureRandom;

/**
 * Utilities for randomizing the numeric keypad.
 *
 * A fresh array is returned so shuffling a keypad never mutates the shared
 * default digit order. SecureRandom makes the layout less predictable to an
 * observer watching repeated PIN entry attempts.
 */
public final class ShuffleArrayUtils {

    private ShuffleArrayUtils() {
        throw new AssertionError("No instances");
    }

    static int[] shuffle(int[] values) {
        if (values == null) {
            throw new IllegalArgumentException("Values must not be null.");
        }
        int[] shuffled = values.clone();
        SecureRandom random = new SecureRandom();
        for (int index = shuffled.length - 1; index > 0; index--) {
            int swapIndex = random.nextInt(index + 1);
            int temporary = shuffled[index];
            shuffled[index] = shuffled[swapIndex];
            shuffled[swapIndex] = temporary;
        }
        return shuffled;
    }
}
