package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.apps.quantitymeasurement.Length.LengthUnit;

public class UnitAdditionTest {

    private static final double EPSILON = 1e-6;

    @Test
    void testAddition_SameUnit_FeetPlusFeet() {
        Length feet1 = new Length(1.0, LengthUnit.FEET);
        Length feet2 = new Length(2.0, LengthUnit.FEET);
        Length result = feet1.add(feet2);

        assertEquals(3.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    void testAddition_SameUnit_InchPlusInch() {
        Length inch1 = new Length(6.0, LengthUnit.INCHES);
        Length inch2 = new Length(6.0, LengthUnit.INCHES);
        Length result = inch1.add(inch2);

        assertEquals(12.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.INCHES, result.getUnit());
    }

    @Test
    void testAddition_CrossUnit_FeetPlusInches() {
        Length feet = new Length(1.0, LengthUnit.FEET);
        Length inch = new Length(12.0, LengthUnit.INCHES);
        Length result = feet.add(inch);

        assertEquals(2.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    void testAddition_CrossUnit_InchPlusFeet() {
        Length inch = new Length(12.0, LengthUnit.INCHES);
        Length feet = new Length(1.0, LengthUnit.FEET);
        Length result = inch.add(feet);

        assertEquals(24.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.INCHES, result.getUnit());
    }

    @Test
    void testAddition_CrossUnit_YardPlusFeet() {
        Length yard = new Length(1.0, LengthUnit.YARDS);
        Length feet = new Length(3.0, LengthUnit.FEET);
        Length result = yard.add(feet);

        assertEquals(2.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.YARDS, result.getUnit());
    }

    @Test
    void testAddition_CrossUnit_CentimeterPlusInch() {
        Length cm = new Length(2.54, LengthUnit.CENTIMETERS);
        Length inch = new Length(1.0, LengthUnit.INCHES);
        Length result = cm.add(inch);

        assertEquals(5.08, result.getValue(), 0.01);
        assertEquals(LengthUnit.CENTIMETERS, result.getUnit());
    }

    @Test
    void testAddition_Commutativity() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertEquals(
                l1.add(l2).convertToBaseUnit(),
                l2.add(l1).convertToBaseUnit(),
                EPSILON
        );
    }

    @Test
    void testAddition_WithZero() {
        Length feet = new Length(5.0, LengthUnit.FEET);
        Length zeroInch = new Length(0.0, LengthUnit.INCHES);
        Length result = feet.add(zeroInch);

        assertEquals(5.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    void testAddition_NegativeValues() {
        Length feet1 = new Length(5.0, LengthUnit.FEET);
        Length feet2 = new Length(-2.0, LengthUnit.FEET);
        Length result = feet1.add(feet2);

        assertEquals(3.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_NullSecondOperand() {
        Length feet = new Length(1.0, LengthUnit.FEET);

        assertThrows(IllegalArgumentException.class, () -> {
            feet.add(null);
        });
    }

    @Test
    void testAddition_LargeValues() {
        Length large1 = new Length(1e6, LengthUnit.FEET);
        Length large2 = new Length(1e6, LengthUnit.FEET);

        assertEquals(2e6, large1.add(large2).getValue(), EPSILON);
    }

    @Test
    void testAddition_SmallValues() {
        Length small1 = new Length(0.001, LengthUnit.FEET);
        Length small2 = new Length(0.002, LengthUnit.FEET);

        assertEquals(0.003, small1.add(small2).getValue(), EPSILON);
    }
}