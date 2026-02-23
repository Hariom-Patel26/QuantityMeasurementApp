package com.apps.quantitymeasurement;

public enum LengthUnit {
//	FEET(conversionFactor: 12.0),
//	FEET(12.0), INCHES(1.0), YARDS(36.0), CENTIMETERS(0.393701);
	// base unit reassigned [Base Unit -> Feet]
	FEET(1.0), 
    INCHES(1.0 / 12.0), 
    YARDS(3.0), 
    CENTIMETERS(1.0 / 30.48);
	private final double conversionFactor;

	// enum to represent different units and their conversion factors with
	// the base unit being inches. This means all the conversion factors are defined
	// in terms of inches.
	LengthUnit(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	public double getConversionFactor() {
		return conversionFactor;
	}
	
	/**
     * Responsibility 1: Convert value in this unit to the base unit (Feet).
     */
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }
//    double convertToBaseUnit() {
//		return value * this.unit.getConversionFactor();
////		double convertedValue = this.value * this.unit.getConversionFactor();
////		return Math.round(convertedValue*100)/100;
//	}

    /**
     * Responsibility 2: Convert a base unit value (Feet) back to this unit.
     */
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / getConversionFactor();
    }
    public double convert(double value, LengthUnit targetUnit) {
        double feetValue = this.convertToBaseUnit(value);
        return feetValue / targetUnit.getConversionFactor();
    }
}