package com.apps.quantitymeasurement.units;

import com.apps.quantitymeasurement.interfaces.IMeasurable;
import com.apps.quantitymeasurement.interfaces.SupportsArithmetic;


public enum LengthUnit implements IMeasurable, SupportsArithmetic {

	FEET(12.0),
	INCHES(1.0),
	YARDS(36.0),
	CENTIMETERS(1 / 2.54);

	private final double conversionFactor;

	LengthUnit(double conversionFactor) {
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
		for(LengthUnit unit : LengthUnit.values()) {			
			if(unit.getUnitName().equalsIgnoreCase(unitName)) {				
				return unit;
			}
		}
		
		throw new IllegalArgumentException(
			"Invalid length unit: " + unitName
		);
	}
}