package com.apps.quantitymeasurement;

import java.util.Objects;

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

	// Implicit Target
	public Quantity<U> add(Quantity<U> that) {
		return add(that, this.unit); // Reuses the overloaded method
	}

	public Quantity<U> add(Quantity<U> that, U targetUnit) {
		// Ensuring non-nullity and finite values
		if (that == null || targetUnit == null) {
			throw new IllegalArgumentException("Operand and target unit cannot be null");
		}
		// Explicit finite check for the current object and the operand
		if (!Double.isFinite(this.value) || !Double.isFinite(that.value)) {
			throw new IllegalArgumentException("Measurement values must be finite");
		}
		return addAndConvert(that, targetUnit);
	}

	private Quantity<U> addAndConvert(Quantity<U> quantity, U targetUnit) {
		double sumInBaseUnit = this.unit.convertToBaseUnit(this.value)
				+ quantity.unit.convertToBaseUnit(quantity.value);

		double finalValue = convertFromBaseToTargetUnit(sumInBaseUnit, targetUnit);
		return new Quantity<U>(finalValue, targetUnit);
	}

	private double convertFromBaseToTargetUnit(double basevalue, U target) {
		return basevalue / target.getConversionFactor();
	}

	public Quantity<U> subtract(Quantity<U> that, U targetUnit) {
		// Fix for NullOperand test
		if (that == null) {
			throw new IllegalArgumentException("Operand cannot be null");
		}

		// Fix for CrossCategory test
		// Runtime check: Are we comparing LengthUnit with WeightUnit?
		if (this.unit.getClass() != that.unit.getClass()) {
			throw new IllegalArgumentException("Cross-category arithmetic is not allowed");
		}

		if (targetUnit == null) {
			throw new IllegalArgumentException("Target unit cannot be null");
		}

		// Logic for subtraction
		double baseDifference = this.unit.convertToBaseUnit(this.value) - that.unit.convertToBaseUnit(that.value);

		double finalValue = targetUnit.convertFromBaseUnit(baseDifference);
		return new Quantity<>(Math.round(finalValue * 100.0) / 100.0, targetUnit);
	}

	// Overloaded Subtraction: Defaults to the unit of the first operand.
	public Quantity<U> subtract(Quantity<U> that) {
		return this.subtract(that, this.unit);
	}

	// cDivision: Returns the dimensionless ratio between two quantities.
	public double divide(Quantity<U> that) {
		validateArithmeticOperand(that);
		double divisorBaseValue = that.unit.convertToBaseUnit(that.value);

		if (Math.abs(divisorBaseValue) < 1e-9) {
			throw new ArithmeticException("Division by zero quantity is not allowed");
		}

		return this.unit.convertToBaseUnit(this.value) / divisorBaseValue;
	}

	// Validation Logic common to all
	private void validateArithmeticOperand(Quantity<U> that) {
		if (that == null)
			throw new IllegalArgumentException("Operand cannot be null");
		// Runtime category safety check
		if (this.unit.getClass() != that.unit.getClass()) {
			throw new IllegalArgumentException("Cross-category arithmetic is not allowed");
		}
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
		return Objects.hash(unit.convertToBaseUnit(value));
	}

}