package com.amirarcane.sample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that the sample module is built with the expected application ID.
 * Functional screen behavior is covered by the device-level test source set.
 */
public class ExampleUnitTest {
    @Test
    public void sampleApplicationIdIsStable() {
        assertEquals("com.amirarcane.sample", BuildConfig.APPLICATION_ID);
    }
}
