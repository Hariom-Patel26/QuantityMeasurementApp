package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.apps.quantitymeasurement.Length.LengthUnit;

public class TargetUnitAdditionTest {

    private static final double EPSILON = 1e-3;

    @Test
    void testAddition_ExplicitTargetUnit_Feet() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);
        Length result = l1.add(l2, LengthUnit.FEET);

        assertEquals(2.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    void testAddition_ExplicitTargetUnit_Inches() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);
        Length result = l1.add(l2, LengthUnit.INCHES);

        assertEquals(24.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.INCHES, result.getUnit());
    }

    @Test
    void testAddition_ExplicitTargetUnit_Yards() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);
        Length result = l1.add(l2, LengthUnit.YARDS);

        assertEquals(0.667, result.getValue(), EPSILON);
        assertEquals(LengthUnit.YARDS, result.getUnit());
    }

    @Test
    void testAddition_ExplicitTargetUnit_Centimeters() {
        Length l1 = new Length(1.0, LengthUnit.INCHES);
        Length l2 = new Length(1.0, LengthUnit.INCHES);
        Length result = l1.add(l2, LengthUnit.CENTIMETERS);

        assertEquals(5.08, result.getValue(), 0.01);
        assertEquals(LengthUnit.CENTIMETERS, result.getUnit());
    }

    @Test
    void testAddition_ExplicitTargetUnit_SameAsFirstOperand() {
        Length l1 = new Length(2.0, LengthUnit.YARDS);
        Length l2 = new Length(3.0, LengthUnit.FEET);
        Length result = l1.add(l2, LengthUnit.YARDS);

        assertEquals(3.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.YARDS, result.getUnit());
    }

    @Test
    void testAddition_ExplicitTargetUnit_SameAsSecondOperand() {
        Length l1 = new Length(2.0, LengthUnit.YARDS);
        Length l2 = new Length(3.0, LengthUnit.FEET);
        Length result = l1.add(l2, LengthUnit.FEET);

        assertEquals(9.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    void testAddition_ExplicitTargetUnit_Commutativity() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        Length res1 = l1.add(l2, LengthUnit.YARDS);
        Length res2 = l2.add(l1, LengthUnit.YARDS);

        assertEquals(res1.getValue(), res2.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_WithZero() {
        Length l1 = new Length(5.0, LengthUnit.FEET);
        Length l2 = new Length(0.0, LengthUnit.INCHES);
        Length result = l1.add(l2, LengthUnit.YARDS);

        assertEquals(1.667, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_NegativeValues() {
        Length l1 = new Length(5.0, LengthUnit.FEET);
        Length l2 = new Length(-2.0, LengthUnit.FEET);
        Length result = l1.add(l2, LengthUnit.INCHES);

        assertEquals(36.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_NullTargetUnit() {
        Length l1 = new Length(1.0, LengthUnit.FEET);

        assertThrows(IllegalArgumentException.class, () -> {
            l1.add(new Length(12.0, LengthUnit.INCHES), null);
        });
    }

    @Test
    void testAddition_ExplicitTargetUnit_LargeToSmallScale() {
        Length l1 = new Length(1000.0, LengthUnit.FEET);
        Length l2 = new Length(500.0, LengthUnit.FEET);
        Length result = l1.add(l2, LengthUnit.INCHES);

        assertEquals(18000.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_SmallToLargeScale() {
        Length l1 = new Length(12.0, LengthUnit.INCHES);
        Length l2 = new Length(12.0, LengthUnit.INCHES);
        Length result = l1.add(l2, LengthUnit.YARDS);

        assertEquals(0.667, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_PrecisionTolerance() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(0.1, LengthUnit.FEET);
        Length result = l1.add(l2, LengthUnit.INCHES);

        assertEquals(13.2, result.getValue(), EPSILON);
    }
}