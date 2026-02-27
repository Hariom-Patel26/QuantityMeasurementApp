package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtractionDivisionTest {

    private static final double EPSILON = 1e-3;

    // Subtraction Operations

    @Test
    void testSubtraction_SameUnit_FeetMinusFeet() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_SameUnit_LitreMinusLitre() {
        Quantity<VolumeUnit> result = new Quantity<>(10.0, VolumeUnit.LITRE)
                .subtract(new Quantity<>(3.0, VolumeUnit.LITRE));
        assertEquals(7.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_CrossUnit_FeetMinusInches() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES));
        assertEquals(9.5, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_CrossUnit_InchesMinusFeet() {
        Quantity<LengthUnit> result = new Quantity<>(120.0, LengthUnit.INCHES)
                .subtract(new Quantity<>(5.0, LengthUnit.FEET));
        assertEquals(60.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_ExplicitTargetUnit_Inches() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES), LengthUnit.INCHES);
        assertEquals(114.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_ResultingInNegative() {
        Quantity<LengthUnit> result = new Quantity<>(5.0, LengthUnit.FEET)
                .subtract(new Quantity<>(10.0, LengthUnit.FEET));
        assertEquals(-5.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_ResultingInZero() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(120.0, LengthUnit.INCHES));
        assertEquals(0.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_WithZeroOperand() {
        Quantity<LengthUnit> result = new Quantity<>(5.0, LengthUnit.FEET)
                .subtract(new Quantity<>(0.0, LengthUnit.INCHES));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_NonCommutative() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(5.0, LengthUnit.FEET);
        assertNotEquals(q1.subtract(q2).getValue(),
                        q2.subtract(q1).getValue(),
                        EPSILON);
    }

    @Test
    void testSubtraction_NullOperand() {
        assertThrows(IllegalArgumentException.class, () ->
                new Quantity<>(10.0, LengthUnit.FEET).subtract(null));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    void testSubtraction_CrossCategory() {
        Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity weight = new Quantity<>(5.0, WeightUnit.KILOGRAM);

        assertThrows(IllegalArgumentException.class, () ->
                length.subtract(weight));
    }

    @Test
    void testSubtraction_ChainedOperations() {
        Quantity<LengthUnit> result = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(2.0, LengthUnit.FEET))
                .subtract(new Quantity<>(1.0, LengthUnit.FEET));
        assertEquals(7.0, result.getValue(), EPSILON);
    }

    // Division Operations

    @Test
    void testDivision_SameUnit_FeetDividedByFeet() {
        double ratio = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(5.0, ratio, EPSILON);
    }

    @Test
    void testDivision_CrossUnit_FeetDividedByInches() {
        double ratio = new Quantity<>(24.0, LengthUnit.INCHES)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(1.0, ratio, EPSILON);
    }

    @Test
    void testDivision_RatioLessThanOne() {
        double ratio = new Quantity<>(5.0, LengthUnit.FEET)
                .divide(new Quantity<>(10.0, LengthUnit.FEET));
        assertEquals(0.5, ratio, EPSILON);
    }

    @Test
    void testDivision_ByZero() {
        assertThrows(ArithmeticException.class, () ->
                new Quantity<>(10.0, LengthUnit.FEET)
                        .divide(new Quantity<>(0.0, LengthUnit.FEET)));
    }

    @Test
    void testDivision_NonCommutative() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(5.0, LengthUnit.FEET);
        assertNotEquals(q1.divide(q2), q2.divide(q1), EPSILON);
    }

    @Test
    void testDivision_Associativity() {
        Quantity<LengthUnit> a = new Quantity<>(100.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(10.0, LengthUnit.FEET);
        double lhs = (a.divide(b)) / 2.0;
        assertNotEquals(lhs, 100.0 / (10.0 / 2.0), EPSILON);
    }

    // Integration & Immutability

    @Test
    void testSubtractionAddition_Inverse() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(2.0, LengthUnit.FEET);
        assertEquals(a.getValue(),
                a.add(b).subtract(b).getValue(),
                EPSILON);
    }

    @Test
    void testSubtraction_Immutability() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
        q.subtract(new Quantity<>(5.0, LengthUnit.FEET));
        assertEquals(10.0, q.getValue(), EPSILON);
    }

    @Test
    void testSubtraction_PrecisionAndRounding() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.111, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(0.111, LengthUnit.FEET);
        assertEquals(1.00, q1.subtract(q2).getValue(), 0.0);
    }
}