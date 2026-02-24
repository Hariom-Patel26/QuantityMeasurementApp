package com.apps.quantitymeasurement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GenericQuantityTest {
    private static final double EPSILON = 1e-3;

    // --- 1. Interface Implementation Tests ---

    @Test
    void testIMeasurableInterface_LengthUnitImplementation() {
        IMeasurable unit = LengthUnit.FEET;
        assertNotNull(unit.convertToBaseUnit(1.0));
    }

    @Test
    void testIMeasurableInterface_WeightUnitImplementation() {
        IMeasurable unit = WeightUnit.KILOGRAM;
        assertNotNull(unit.convertToBaseUnit(1.0));
    }

    @Test
    void testIMeasurableInterface_ConsistentBehavior() {
        assertTrue(LengthUnit.FEET instanceof IMeasurable);
        assertTrue(WeightUnit.KILOGRAM instanceof IMeasurable);
    }

    // --- 2. Generic Quantity Operations ---

    @Test
    void testGenericQuantity_LengthOperations_Equality() {
        Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> inches = new Quantity<>(12.0, LengthUnit.INCHES);
        assertTrue(feet.equals(inches));
    }

    @Test
    void testGenericQuantity_WeightOperations_Equality() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> grams = new Quantity<>(1000.0, WeightUnit.GRAM);
        assertTrue(kg.equals(grams));
    }

    @Test
    void testGenericQuantity_LengthOperations_Conversion() {
        Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> result = feet.convertTo(LengthUnit.INCHES);
        assertEquals(12.0, result.getValue(), EPSILON);
    }

    @Test
    void testGenericQuantity_WeightOperations_Conversion() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> result = kg.convertTo(WeightUnit.GRAM);
        assertEquals(1000.0, result.getValue(), EPSILON);
    }

    @Test
    void testGenericQuantity_LengthOperations_Addition() {
        Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> inches = new Quantity<>(12.0, LengthUnit.INCHES);
        Quantity<LengthUnit> sum = feet.add(inches, LengthUnit.FEET);
        assertEquals(2.0, sum.getValue(), EPSILON);
    }

    @Test
    void testGenericQuantity_WeightOperations_Addition() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> grams = new Quantity<>(1000.0, WeightUnit.GRAM);
        Quantity<WeightUnit> sum = kg.add(grams, WeightUnit.KILOGRAM);
        assertEquals(2.0, sum.getValue(), EPSILON);
    }

    // --- 3. Cross-Category Prevention & Safety ---

    @Test
    void testCrossCategoryPrevention_LengthVsWeight() {
        Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertFalse(feet.equals(kg));
    }

    @Test
    void testTypeErasure_RuntimeSafety() {
        Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
        Object weight = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertFalse(feet.equals(weight));
    }

    // --- 4. Validation & Edge Cases ---

    @Test
    void testGenericQuantity_ConstructorValidation_NullUnit() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity<LengthUnit>(1.0, null));
    }

    @Test
    void testGenericQuantity_ConstructorValidation_InvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity<LengthUnit>(Double.NaN, LengthUnit.FEET));
    }

    @Test
    void testImmutability_GenericQuantity() {
        Quantity<LengthUnit> original = new Quantity<>(1.0, LengthUnit.FEET);
        original.convertTo(LengthUnit.INCHES);
        assertEquals(1.0, original.getValue(), EPSILON);
    }

    // --- 5. Scalability & Advanced Generic Tests ---

    enum VolumeUnit implements IMeasurable {
        LITER(1.0);

        private final double factor;

        VolumeUnit(double f) {
            this.factor = f;
        }

        public double getConversionFactor() {
            return factor;
        }

        public double convertToBaseUnit(double v) {
            return v * factor;
        }

        public double convertFromBaseUnit(double b) {
            return b / factor;
        }

        @Override
        public String getUnitName() {
            return "LITER";
        }
    }

    @Test
    void testScalability_NewUnitEnumIntegration() {
        Quantity<VolumeUnit> liter = new Quantity<>(1.0, VolumeUnit.LITER);
        assertEquals(1.0, liter.getValue(), EPSILON);
    }

    @Test
    void testHashCode_GenericQuantity_Consistency() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void testTypeWildcard_FlexibleSignatures() {
        assertTrue(isPositive(new Quantity<>(1.0, LengthUnit.FEET)));
        assertTrue(isPositive(new Quantity<>(1.0, WeightUnit.KILOGRAM)));
    }

    private boolean isPositive(Quantity<?> q) {
        return q.getValue() > 0;
    }

    @Test
    void testMaintainability_SingleSourceOfTruth() {
        Quantity<LengthUnit> lResult = new Quantity<>(1.0, LengthUnit.FEET)
                .add(new Quantity<>(1.0, LengthUnit.FEET), LengthUnit.FEET);
        Quantity<WeightUnit> wResult = new Quantity<>(1.0, WeightUnit.KILOGRAM)
                .add(new Quantity<>(1.0, WeightUnit.KILOGRAM), WeightUnit.KILOGRAM);
        assertEquals(2.0, lResult.getValue(), EPSILON);
        assertEquals(2.0, wResult.getValue(), EPSILON);
    }
}
