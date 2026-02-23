package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ExtendedUnitTest {

    // Yard to Yard (Same Value)
    @Test
    void testEquality_YardToYard_SameValue() {
        assertEquals(
                new Length(1.0, Length.LengthUnit.YARDS),
                new Length(1.0, Length.LengthUnit.YARDS)
        );
    }

    // Yard to Yard (Different Value)
    @Test
    void testEquality_YardToYard_DifferentValue() {
        assertNotEquals(
                new Length(1.0, Length.LengthUnit.YARDS),
                new Length(2.0, Length.LengthUnit.YARDS)
        );
    }

    // Yard to Feet (Equivalent)
    @Test
    void testEquality_YardToFeet_EquivalentValue() {
        assertEquals(
                new Length(1.0, Length.LengthUnit.YARDS),
                new Length(3.0, Length.LengthUnit.FEET)
        );
    }

    // Feet to Yard (Equivalent)
    @Test
    void testEquality_FeetToYard_EquivalentValue() {
        assertEquals(
                new Length(3.0, Length.LengthUnit.FEET),
                new Length(1.0, Length.LengthUnit.YARDS)
        );
    }

    // Yard to Inches (Equivalent)
    @Test
    void testEquality_YardToInches_EquivalentValue() {
        assertEquals(
                new Length(1.0, Length.LengthUnit.YARDS),
                new Length(36.0, Length.LengthUnit.INCHES)
        );
    }

    // Inches to Yard (Equivalent)
    @Test
    void testEquality_InchesToYard_EquivalentValue() {
        assertEquals(
                new Length(36.0, Length.LengthUnit.INCHES),
                new Length(1.0, Length.LengthUnit.YARDS)
        );
    }

    // Yard to Feet (Non Equivalent)
    @Test
    void testEquality_YardToFeet_NonEquivalentValue() {
        assertNotEquals(
                new Length(1.0, Length.LengthUnit.YARDS),
                new Length(2.0, Length.LengthUnit.FEET)
        );
    }

    // Centimeter to Inches (Equivalent)
    @Test
    void testEquality_CentimeterToInches_EquivalentValue() {
        assertEquals(
                new Length(1.0, Length.LengthUnit.CENTIMETERS),
                new Length(0.393701, Length.LengthUnit.INCHES)
        );
    }

    // Centimeter to Feet (Non Equivalent)
    @Test
    void testEquality_CentimeterToFeet_NonEquivalentValue() {
        assertNotEquals(
                new Length(1.0, Length.LengthUnit.CENTIMETERS),
                new Length(1.0, Length.LengthUnit.FEET)
        );
    }

    // Transitive Property
    @Test
    void testEquality_MultiUnit_TransitiveProperty() {
        Length yard = new Length(1.0, Length.LengthUnit.YARDS);
        Length feet = new Length(3.0, Length.LengthUnit.FEET);
        Length inches = new Length(36.0, Length.LengthUnit.INCHES);

        assertEquals(yard, feet);
        assertEquals(feet, inches);
        assertEquals(yard, inches);
    }

    //  Yard with Null Unit
    @Test
    void testEquality_YardWithNullUnit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Length(1.0, null)
        );
    }

    //  Yard Same Reference (Reflexive)
    @Test
    void testEquality_YardSameReference() {
        Length yard = new Length(2.0, Length.LengthUnit.YARDS);
        assertEquals(yard, yard);
    }

    //  Yard Null Comparison
    @Test
    void testEquality_YardNullComparison() {
        Length yard = new Length(2.0, Length.LengthUnit.YARDS);
        assertNotEquals(yard, null);
    }

    // Centimeter with Null Unit
    @Test
    void testEquality_CentimeterWithNullUnit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Length(1.0, null)
        );
    }

    // Centimeter Same Reference
    @Test
    void testEquality_CentimeterSameReference() {
        Length cm = new Length(5.0, Length.LengthUnit.CENTIMETERS);
        assertEquals(cm, cm);
    }

    // Centimeter Null Comparison
    @Test
    void testEquality_CentimeterNullComparison() {
        Length cm = new Length(5.0, Length.LengthUnit.CENTIMETERS);
        assertNotEquals(cm, null);
    }

    // Complex Multi-Unit Scenario
    @Test
    void testEquality_AllUnits_ComplexScenario() {
        assertEquals(
                new Length(2.0, Length.LengthUnit.YARDS),
                new Length(6.0, Length.LengthUnit.FEET)
        );

        assertEquals(
                new Length(6.0, Length.LengthUnit.FEET),
                new Length(72.0, Length.LengthUnit.INCHES)
        );
    }
}