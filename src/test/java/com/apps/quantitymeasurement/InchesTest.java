package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class InchesTest {

    @Test
    void testEquality_SameValue() {
        Inches inch1 = new Inches(5.0);
        Inches inch2 = new Inches(5.0);

        assertTrue(inch1.equals(inch2));
    }

    @Test
    void testEquality_DifferentValue() {
        Inches inch1 = new Inches(5.0);
        Inches inch2 = new Inches(6.0);

        assertFalse(inch1.equals(inch2));
    }

    @Test
    void testEquality_NullComparison() {
        Inches inch1 = new Inches(5.0);

        assertFalse(inch1.equals(null));
    }

    @Test
    void testEquality_SameReference() {
        Inches inch1 = new Inches(5.0);

        assertTrue(inch1.equals(inch1));
    }

    @Test
    void testEquality_DifferentType() {
        Inches inch1 = new Inches(5.0);

        assertFalse(inch1.equals("5.0"));
    }
}