package com.apps.quantitymeasurement;

public enum WeightUnit implements IMeasurable {
	KILOGRAM(1.0), GRAM(0.001), POUND(0.453592);

	private final double conversionFactor;

	WeightUnit(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	public double getConversionFactor() {
		return conversionFactor;
	}

	// Responsibility 1: Convert value in this unit to the base unit (Feet).
	@Override
	public double convertToBaseUnit(double value) {
		return value * conversionFactor;
	}

	// Responsibility 2: Convert a base unit value (Feet) back to this unit.

	@Override
	public double convertFromBaseUnit(double baseValue) {
		return baseValue / conversionFactor;
	}

	public double convert(double value, LengthUnit targetUnit) {
		double feetValue = this.convertToBaseUnit(value);
		return feetValue / targetUnit.conversionFactor;
	}

//	@Override
//	public String getUnitName() {
//		return this.getUnitName();
//	}
}