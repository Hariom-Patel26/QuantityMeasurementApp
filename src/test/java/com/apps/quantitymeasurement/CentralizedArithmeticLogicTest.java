package com.apps.quantitymeasurement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CentralizedArithmeticLogicTest {

	private static final double EPSILON = 1e-3;

	// Enum Logic Verification ---

	@Test
	void testArithmeticOperation_Add_EnumComputation() {
		Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
		Quantity<LengthUnit> result = q.add(new Quantity<>(5.0, LengthUnit.FEET));
		assertEquals(15.0, result.getValue(), EPSILON);
	}

	@Test
	void testArithmeticOperation_Subtract_EnumComputation() {
		Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
		Quantity<LengthUnit> result = q.subtract(new Quantity<>(5.0, LengthUnit.FEET));
		assertEquals(5.0, result.getValue(), EPSILON);
	}

	@Test
	void testArithmeticOperation_DivideByZero_EnumThrows() {
		Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
		Quantity<LengthUnit> q2 = new Quantity<>(0.0, LengthUnit.FEET);

		assertThrows(ArithmeticException.class, () -> q1.divide(q2));
	}

	// Validation Consistency (DRY Verification) ---

	@Test
	void testValidation_NullOperand_ConsistentAcrossOperations() {
		Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);

		IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> q.add(null));
		assertEquals("Operand cannot be null", ex1.getMessage());

		IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> q.subtract(null));
		assertEquals("Operand cannot be null", ex2.getMessage());

		IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> q.divide(null));
		assertEquals("Operand cannot be null", ex3.getMessage());
	}

	@Test
	void testValidation_CrossCategory_ConsistentAcrossOperations() {
		Quantity<LengthUnit> length = new Quantity<>(10.0, LengthUnit.FEET);
		Quantity<WeightUnit> weight = new Quantity<>(5.0, WeightUnit.KILOGRAM);
		String expectedMsg = "Cross-category arithmetic is not allowed";

		@SuppressWarnings("rawtypes")
		Quantity rawWeight = (Quantity) weight;

		@SuppressWarnings("unchecked")
		IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> length.add(rawWeight));
		assertEquals(expectedMsg, ex1.getMessage());

		@SuppressWarnings("unchecked")
		IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> length.subtract(rawWeight));
		assertEquals(expectedMsg, ex2.getMessage());

		@SuppressWarnings("unchecked")
		IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> length.divide(rawWeight));
		assertEquals(expectedMsg, ex3.getMessage());
	}

	// Rounding & Precision Consistency ---

	@Test
	void testRounding_AddSubtract_TwoDecimalPlaces() {
		Quantity<LengthUnit> q1 = new Quantity<>(1.111, LengthUnit.FEET);
		Quantity<LengthUnit> q2 = new Quantity<>(2.222, LengthUnit.FEET);

		assertEquals(3.33, q1.add(q2).getValue(), 0.0);
		assertEquals(-1.11, q1.subtract(q2).getValue(), 0.0);
	}

	@Test
	void testRounding_Divide_NoRounding() {
		Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
		Quantity<LengthUnit> q2 = new Quantity<>(3.0, LengthUnit.FEET);

		assertNotEquals(3.33, q1.divide(q2), 0.0);
		assertEquals(3.3333, q1.divide(q2), 0.0001);
	}

	// Backward Compatibility & Chaining ---

	@Test
	void testArithmetic_Chain_Operations() {
		Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
		Quantity<LengthUnit> q2 = new Quantity<>(5.0, LengthUnit.FEET);
		Quantity<LengthUnit> q3 = new Quantity<>(2.0, LengthUnit.FEET);
		Quantity<LengthUnit> result = q1.add(q2).subtract(q3);
		assertEquals(13.0, result.getValue(), EPSILON);
	}

	@Test
	void testImmutability_Maintained() {
		Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
		q1.add(new Quantity<>(5.0, LengthUnit.FEET));

		assertEquals(10.0, q1.getValue(), EPSILON);
	}
}