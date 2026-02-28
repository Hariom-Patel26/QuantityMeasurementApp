package com.apps.quantitymeasurement;

import java.util.function.DoubleBinaryOperator;

public class Quantity<U extends IMeasurable> {
	private double value;
	private U unit;

	public Quantity(double value, U unit) {
		if (unit == null) {
			throw new IllegalArgumentException("Unit cannot be null");
		}
		if (!Double.isFinite(value)) {
			throw new IllegalArgumentException("Value must be finite");
		}
		this.unit = unit;
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public U getUnit() {
		return unit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		// Use a wildcard to handle different generic types at runtime
		Quantity<?> that = (Quantity<?>) o;

		if (this.unit.getClass() != that.unit.getClass()) {
			return false;
		}

		// Now compare base values using epsilon for floating-point precision
		return Math.abs(this.unit.convertToBaseUnit(this.value) - that.unit.convertToBaseUnit(that.value)) < 1e-3;
	}

	public Quantity<U> convertTo(U targetUnit) {
		if (targetUnit == null) {
			throw new IllegalArgumentException("Target unit cannot be null");
		}
		if (!targetUnit.getClass().equals(unit.getClass())) {
			throw new IllegalArgumentException("Target unit should belong to same class");
		}
		double baseValue = unit.convertToBaseUnit(value);
		double convertValue = targetUnit.convertFromBaseUnit(baseValue);
		return new Quantity<U>(convertValue, targetUnit);
	}

	@Override
	public String toString() {
		return String.format("%.2f %s", value, unit);
	}

//	UC13: Private enum to encapsulate arithmetic logic using Lambdas. Adheres to the Open/Closed Principle for future operations
	
	private enum ArithmeticOperation {
		ADD((a, b) -> a + b), SUBTRACT((a, b) -> a - b), DIVIDE((a, b) -> {
			if (Math.abs(b) < 1e-9)
				throw new ArithmeticException("Division by zero");
			return a / b;
		});

		private final DoubleBinaryOperator operation;

		ArithmeticOperation(DoubleBinaryOperator operation) {
			this.operation = operation;
		}

		public double compute(double v1, double v2) {
			return operation.applyAsDouble(v1, v2);
		}
	}

	 // UC13: Centralized validation to enforce consistent error handling.
	 
	private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetUnitRequired) {

		if (other == null)
			throw new IllegalArgumentException("Operand cannot be null");
		if (this.unit.getClass() != other.unit.getClass()) {
			throw new IllegalArgumentException("Cross-category arithmetic is not allowed");
		}
		if (targetUnitRequired && targetUnit == null) {
			throw new IllegalArgumentException("Target unit cannot be null");
		}
		// UC14: Check if the unit supports arithmetic (e.g., addition)
		this.unit.validateOperationSupport("Arithmetic");
	}


	 // UC13: Centralized arithmetic helper to eliminate code duplication.

	private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
		double v1 = this.unit.convertToBaseUnit(this.value);
		double v2 = other.unit.convertToBaseUnit(other.value);
		return operation.compute(v1, v2);
	}

	// Implicit Target
	public Quantity<U> add(Quantity<U> that) {
		return add(that, this.unit); // Reuses the overloaded method
	}

	// Adds two measurements and returns the result in a specified target unit.
	public Quantity<U> add(Quantity<U> other, U targetUnit) {
		validateArithmeticOperands(other, targetUnit, true);
		double baseResult = performBaseArithmetic(other, ArithmeticOperation.ADD);
		double finalValue = targetUnit.convertFromBaseUnit(baseResult);
		return new Quantity<>(round(finalValue), targetUnit);
	}

	public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
		validateArithmeticOperands(other, targetUnit, true);
		double baseResult = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
		double finalValue = targetUnit.convertFromBaseUnit(baseResult);
		return new Quantity<>(round(finalValue), targetUnit);
	}

	// Overloaded Subtraction: Defaults to the unit of the first operand.
	public Quantity<U> subtract(Quantity<U> that) {
		return this.subtract(that, this.unit);
	}

	// Division: Returns the dimension less ratio between two quantities.
	public double divide(Quantity<U> other) {
		validateArithmeticOperands(other, null, false);
		return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
	}

	// Simple rounding helper to maintain precision
	private double round(double val) {
		return Math.round(val * 100.0) / 100.0;
	}

	public static void main(String[] args) {
		Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
		Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCHES);
		System.out.println("1 feet == 12 inches ??" + l1.equals(l2));

		Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
		Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);
		System.out.println("1 kg = 1000 grams ?" + w1.equals(w2));
	}

	public boolean compare(Quantity<?> q2) {
		if (q2 == null) {
			return false;
		}
		return Double.compare(this.unit.convertToBaseUnit(this.value), q2.unit.convertToBaseUnit(q2.value)) == 0;
	}

	@Override
	public int hashCode() {
	    double baseValue = unit.convertToBaseUnit(value);
	    long normalized = Math.round(baseValue * 1000);
	    return Long.hashCode(normalized);
	}

}