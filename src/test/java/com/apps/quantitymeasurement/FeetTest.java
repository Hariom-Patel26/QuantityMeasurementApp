package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeetTest {

    @Test
    void testEquality_SameValue() {

        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        assertTrue(feet1.equals(feet2),
                "1.0 ft should be equal to 1.0 ft");
    }

    @Test
    void testEquality_DifferentValue() {

        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(2.0);

        assertFalse(feet1.equals(feet2),
                "1.0 ft should NOT be equal to 2.0 ft");
    }

    @Test
    void testEquality_NullComparison() {

        Feet feet = new Feet(1.0);

        assertFalse(feet.equals(null),
                "Feet should not be equal to null");
    }

    @Test
    void testEquality_NonNumericInput() {

        Feet feet = new Feet(1.0);

        assertFalse(feet.equals("1.0"),
                "Feet should not be equal to String");
    }

    @Test
    void testEquality_SameReference() {

        Feet feet = new Feet(1.0);

        assertTrue(feet.equals(feet),
                "Object must be equal to itself");
    }
}