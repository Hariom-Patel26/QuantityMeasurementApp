package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.Length.LengthUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LengthTest {

    // 1. Feet to Feet same value
    @Test
    void testEquality_FeetToFeet_SameValue() {
        assertTrue(new Length(1.0, LengthUnit.FEET)
                .equals(new Length(1.0, LengthUnit.FEET)));
    }

    // 2. Inches to Inches same value
    @Test
    void testEquality_InchesToInches_SameValue() {
        assertTrue(new Length(5.0, LengthUnit.INCHES)
                .equals(new Length(5.0, LengthUnit.INCHES)));
    }

    // 3. Feet to Inches equivalent
    @Test
    void testEquality_FeetToInches_Equivalent() {
        assertTrue(new Length(1.0, LengthUnit.FEET)
                .equals(new Length(12.0, LengthUnit.INCHES)));
    }

    // 4. Inches to Feet equivalent (Symmetry)
    @Test
    void testEquality_InchesToFeet_Equivalent() {
        assertTrue(new Length(12.0, LengthUnit.INCHES)
                .equals(new Length(1.0, LengthUnit.FEET)));
    }

    // 5. Feet different values
    @Test
    void testEquality_FeetDifferent() {
        assertFalse(new Length(1.0, LengthUnit.FEET)
                .equals(new Length(2.0, LengthUnit.FEET)));
    }

    // 6. Inches different values
    @Test
    void testEquality_InchesDifferent() {
        assertFalse(new Length(10.0, LengthUnit.INCHES)
                .equals(new Length(5.0, LengthUnit.INCHES)));
    }

    // 7. Same reference (Reflexive)
    @Test
    void testEquality_SameReference() {
        Length l = new Length(3.0, LengthUnit.FEET);
        assertTrue(l.equals(l));
    }

    // 8. Null comparison
    @Test
    void testEquality_NullComparison() {
        assertFalse(new Length(1.0, LengthUnit.FEET).equals(null));
    }

    // 9. Different object type
    @Test
    void testEquality_DifferentType() {
        assertFalse(new Length(1.0, LengthUnit.FEET)
                .equals("Not a Length"));
    }

    // 10. Zero values
    @Test
    void testEquality_ZeroValues() {
        assertTrue(new Length(0.0, LengthUnit.FEET)
                .equals(new Length(0.0, LengthUnit.INCHES)));
    }

    // 11. Negative values
    @Test
    void testEquality_NegativeValues() {
        assertTrue(new Length(-1.0, LengthUnit.FEET)
                .equals(new Length(-12.0, LengthUnit.INCHES)));
    }

    // 12. Constructor null unit
    @Test
    void testConstructor_NullUnit() {
        assertThrows(IllegalArgumentException.class, () ->
                new Length(1.0, null));
    }

    // 13. Symmetric property
    @Test
    void testSymmetricProperty() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertTrue(l1.equals(l2) && l2.equals(l1));
    }

    // 14. Transitive property
    @Test
    void testTransitiveProperty() {
        Length a = new Length(1.0, LengthUnit.FEET);
        Length b = new Length(12.0, LengthUnit.INCHES);
        Length c = new Length(1.0, LengthUnit.FEET);

        assertTrue(a.equals(b) && b.equals(c) && a.equals(c));
    }

    // 15. Consistent property
    @Test
    void testConsistentProperty() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertTrue(l1.equals(l2));
        assertTrue(l1.equals(l2)); // repeated call
    }

    // 16. hashCode contract
    @Test
    void testHashCode_EqualObjects() {
        Length l1 = new Length(1.0, LengthUnit.FEET);
        Length l2 = new Length(12.0, LengthUnit.INCHES);

        assertEquals(l1.hashCode(), l2.hashCode());
    }
}