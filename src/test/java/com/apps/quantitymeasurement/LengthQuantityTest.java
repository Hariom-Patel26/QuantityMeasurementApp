package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LengthQuantityTest {

    @Test
    void testEquality_FeetToFeet_SameValue() {
        Length feet1 = new Length(1.0, LengthUnit.FEET);
        Length feet2 = new Length(1.0, LengthUnit.FEET);
        assertTrue(feet1.equals(feet2));
    }

    @Test
    void testEquality_InchToInch_SameValue() {
        Length inch1 = new Length(1.0, LengthUnit.INCHES);
        Length inch2 = new Length(1.0, LengthUnit.INCHES);
        assertTrue(inch1.equals(inch2));
    }

    @Test
    void testEquality_FeetToInch_Equivalency() {
        Length feet1 = new Length(1.0, LengthUnit.FEET);
        Length inch1 = new Length(12.0, LengthUnit.INCHES);
        assertTrue(feet1.equals(inch1));
    }

    @Test
    void testEquality_InchToFeet_EquivalentValue() {
        Length feet1 = new Length(1.0, LengthUnit.FEET);
        Length inch1 = new Length(12.0, LengthUnit.INCHES);
        assertTrue(inch1.equals(feet1));
    }

    @Test
    void testEquality_FeetToFeet_DifferentValue() {
        Length feet1 = new Length(1.0, LengthUnit.FEET);
        Length feet2 = new Length(15.0, LengthUnit.FEET);
        assertFalse(feet1.equals(feet2));
    }

    @Test
    void testEquality_InchToInch_DifferentValue() {
        Length inch1 = new Length(1.0, LengthUnit.INCHES);
        Length inch2 = new Length(14.0, LengthUnit.INCHES);
        assertFalse(inch1.equals(inch2));
    }

    @Test
    void testEquality_InvalidUnit() {
        Length feet = new Length(1.0, LengthUnit.FEET);
        Object notALength = new Object();
        assertFalse(feet.equals(notALength));
    }

    @Test
    void testEquality_NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Length(12.0, null));
    }

    @Test
    void testEquality_SameReference() {
        Length feet1 = new Length(12.0, LengthUnit.FEET);
        assertTrue(feet1.equals(feet1));
    }

    @Test
    void testEquality_NullComparison1() {
        Length feet1 = new Length(12.0, LengthUnit.FEET);
        assertFalse(feet1.equals(null));
    }
}
