package com.apps.quantitymeasurement.units;

import com.apps.quantitymeasurement.interfaces.IMeasurable;
import com.apps.quantitymeasurement.interfaces.SupportsArithmetic;

public enum WeightUnit implements IMeasurable, SupportsArithmetic {
	
	KILOGRAM(1.0), 
	GRAM(0.001), 
	POUND(0.453592);

	private final double conversionFactor;

	WeightUnit(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	@Override
	public double convertToBaseUnit(double value) {
		double result = value * conversionFactor;
		return Math.round(result * 1_000_000.0) / 1_000_000.0;
	}

	@Override
	public double convertFromBaseUnit(double baseValue) {
		double result = baseValue / conversionFactor;
		return Math.round(result * 1_000_000.0) / 1_000_000.0;
	}
	
	@Override
	public String getUnitName() {
		return name();
	}
	
	@Override
	public String getMeasurementType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IMeasurable getUnitInstance(String unitName) {
		for (WeightUnit unit : WeightUnit.values()) {			
			if (unit.getUnitName().equalsIgnoreCase(unitName)) {				
				return unit;
			}
		}
		
		throw new IllegalArgumentException(
			"Invalid weight unit: " + unitName
		);
	}
}