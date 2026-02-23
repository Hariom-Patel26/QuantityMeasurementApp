package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class WeightMeasurementTest {

    private static final double EPSILON = 1e-3;

    // --- Equality Comparisons ---

    @Test
    void testEquality_KilogramToKilogram_SameValue() {
        assertTrue(new Weight(1.0, WeightUnit.KILOGRAM).equals(new Weight(1.0, WeightUnit.KILOGRAM)));
    }

    @Test
    void testEquality_KilogramToKilogram_DifferentValue() {
        assertFalse(new Weight(1.0, WeightUnit.KILOGRAM).equals(new Weight(2.0, WeightUnit.KILOGRAM)));
    }

    @Test
    void testEquality_KilogramToGram_EquivalentValue() {
        assertTrue(new Weight(1.0, WeightUnit.KILOGRAM).equals(new Weight(1000.0, WeightUnit.GRAM)));
    }

    @Test
    void testEquality_GramToKilogram_EquivalentValue() {
        assertTrue(new Weight(1000.0, WeightUnit.GRAM).equals(new Weight(1.0, WeightUnit.KILOGRAM)));
    }

    @Test
    void testEquality_WeightVsLength_Incompatible() {
        Weight weight = new Weight(1.0, WeightUnit.KILOGRAM);
        Length length = new Length(1.0, LengthUnit.FEET);
        assertFalse(weight.equals(length));
    }

    @Test
    void testEquality_NullComparison() {
        assertFalse(new Weight(1.0, WeightUnit.KILOGRAM).equals(null));
    }

    @Test
    void testEquality_SameReference() {
        Weight weight = new Weight(1.0, WeightUnit.KILOGRAM);
        assertTrue(weight.equals(weight));
    }

    @Test
    void testEquality_NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Weight(1.0, null));
    }

    @Test
    void testEquality_TransitiveProperty() {
        Weight a = new Weight(1.0, WeightUnit.KILOGRAM);
        Weight b = new Weight(1000.0, WeightUnit.GRAM);
        Weight c = new Weight(2.20462, WeightUnit.POUND);
        if (a.equals(b) && b.equals(c)) {
            assertTrue(a.equals(c));
        }
    }

    @Test
    void testEquality_ZeroValue() {
        assertTrue(new Weight(0.0, WeightUnit.KILOGRAM).equals(new Weight(0.0, WeightUnit.GRAM)));
    }

    @Test
    void testEquality_NegativeWeight() {
        assertTrue(new Weight(-1.0, WeightUnit.KILOGRAM).equals(new Weight(-1000.0, WeightUnit.GRAM)));
    }

    @Test
    void testEquality_LargeWeightValue() {
        assertTrue(new Weight(1000000.0, WeightUnit.GRAM).equals(new Weight(1000.0, WeightUnit.KILOGRAM)));
    }

    @Test
    void testEquality_SmallWeightValue() {
        assertTrue(new Weight(0.001, WeightUnit.KILOGRAM).equals(new Weight(1.0, WeightUnit.GRAM)));
    }

    // --- Unit Conversions ---

    @Test
    void testConversion_PoundToKilogram() {
        Weight result = new Weight(2.20462, WeightUnit.POUND).convertTo(WeightUnit.KILOGRAM);
        assertEquals(1.0, result.getValue(), EPSILON);
    }

    @Test
    void testConversion_KilogramToPound() {
        Weight result = new Weight(1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.POUND);
        assertEquals(2.20462, result.getValue(), EPSILON);
    }

    @Test
    void testConversion_SameUnit() {
        Weight result = new Weight(5.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.KILOGRAM);
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    void testConversion_ZeroValue() {
        assertEquals(0.0, new Weight(0.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM).getValue(), EPSILON);
    }

    @Test
    void testConversion_NegativeValue() {
        assertEquals(-1000.0, new Weight(-1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM).getValue(), EPSILON);
    }

    @Test
    void testConversion_RoundTrip() {
        Weight original = new Weight(1.5, WeightUnit.KILOGRAM);
        Weight roundTrip = original.convertTo(WeightUnit.GRAM).convertTo(WeightUnit.KILOGRAM);
        assertEquals(original.getValue(), roundTrip.getValue(), EPSILON);
    }

    // --- Addition Operations ---

    @Test
    void testAddition_SameUnit_KilogramPlusKilogram() {
        Weight result = new Weight(1.0, WeightUnit.KILOGRAM).add(new Weight(2.0, WeightUnit.KILOGRAM));
        assertEquals(3.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_CrossUnit_KilogramPlusGram() {
        Weight result = new Weight(1.0, WeightUnit.KILOGRAM).add(new Weight(1000.0, WeightUnit.GRAM));
        assertEquals(2.0, result.getValue(), EPSILON);
        assertEquals(WeightUnit.KILOGRAM, result.getUnit());
    }

    @Test
    void testAddition_CrossUnit_GramPlusKilogram() {
        Weight result = new Weight(500.0, WeightUnit.GRAM).add(new Weight(0.5, WeightUnit.KILOGRAM));
        assertEquals(1000.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_CrossUnit_PoundPlusKilogram() {
        Weight result = new Weight(2.20462, WeightUnit.POUND).add(new Weight(1.0, WeightUnit.KILOGRAM));
        assertEquals(4.40924, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_Gram() {
        Weight result = new Weight(1.0, WeightUnit.KILOGRAM).add(new Weight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM);
        assertEquals(2000.0, result.getValue(), EPSILON);
        assertEquals(WeightUnit.GRAM, result.getUnit());
    }

    @Test
    void testAddition_Commutativity() {
        Weight w1 = new Weight(1.0, WeightUnit.KILOGRAM);
        Weight w2 = new Weight(1000.0, WeightUnit.GRAM);
        assertEquals(w1.add(w2, WeightUnit.KILOGRAM).getValue(),
                     w2.add(w1, WeightUnit.KILOGRAM).getValue(),
                     EPSILON);
    }

    @Test
    void testAddition_WithZero() {
        Weight result = new Weight(5.0, WeightUnit.KILOGRAM).add(new Weight(0.0, WeightUnit.GRAM));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_NegativeValues() {
        Weight result = new Weight(5.0, WeightUnit.KILOGRAM).add(new Weight(-2000.0, WeightUnit.GRAM));
        assertEquals(3.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_LargeValues() {
        Weight result = new Weight(1e6, WeightUnit.KILOGRAM).add(new Weight(1e6, WeightUnit.KILOGRAM));
        assertEquals(2e6, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> new Weight(Double.NaN, WeightUnit.KILOGRAM));
    }
}