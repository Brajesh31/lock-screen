package com.amirarcane.lockscreen.andrognito.pinlockview;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ShuffleArrayUtilsTest {

    @Test
    public void shuffleReturnsEveryDigitExactlyOnce() {
        int[] shuffled = ShuffleArrayUtils.shuffle(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        int[] sorted = shuffled.clone();
        Arrays.sort(sorted);

        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, sorted);
    }

    @Test
    public void shuffleDoesNotMutateCallerArray() {
        int[] original = new int[] { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 };
        int[] expected = original.clone();

        ShuffleArrayUtils.shuffle(original);

        assertArrayEquals(expected, original);
    }

    @Test
    public void shuffleReturnsAnIndependentArray() {
        int[] original = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        int[] shuffled = ShuffleArrayUtils.shuffle(original);

        assertNotSame(original, shuffled);
        assertEquals(10, shuffled.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shuffleRejectsNullInput() {
        ShuffleArrayUtils.shuffle(null);
    }
}
