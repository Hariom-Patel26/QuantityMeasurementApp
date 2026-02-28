package com.apps.quantitymeasurement;

public enum LengthUnit implements IMeasurable{

	FEET(1.0), 
    INCHES(1.0 / 12.0), 
    YARDS(3.0), 
    CENTIMETERS(1.0 / 30.48);
	final double conversionFactor;

	LengthUnit(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	public double getConversionFactor() {
		return conversionFactor;
	}

	//Responsibility 1: Convert value in this unit to the base unit (Feet).
	
	@Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

	@Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }
    public double convert(double value, LengthUnit targetUnit) {
        double feetValue = this.convertToBaseUnit(value);
        return feetValue / targetUnit.conversionFactor;
    }
    
/*  @Override
    public String getUnitName() {
    	return this.getUnitName();
   }
*/
}