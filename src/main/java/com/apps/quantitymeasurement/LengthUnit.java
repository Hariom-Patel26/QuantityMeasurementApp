package com.apps.quantitymeasurement;

public enum LengthUnit implements IMeasurable{
	
//	FEET(conversionFactor: 12.0),
//	FEET(12.0), INCHES(1.0), YARDS(36.0), CENTIMETERS(0.393701);
	// base unit reassigned [Base Unit -> Feet]
	
	FEET(1.0), 
    INCHES(1.0 / 12.0), 
    YARDS(3.0), 
    CENTIMETERS(1.0 / 30.48);
	
	private final double conversionFactor;

	LengthUnit(double conversionFactor) {
		this.conversionFactor = conversionFactor;
	}
	
	@Override
	public double getConversionFactor() {
		return conversionFactor;
	}

	@Override
    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }
	
/*    double convertToBaseUnit() {
		return value * this.unit.conversionFactor;
		double convertedValue = this.value * this.unit.conversionFactor;
		return Math.round(convertedValue*100)/100;
	}
*/

	@Override
    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }
	
    public double convert(double value, LengthUnit targetUnit) {
        double feetValue = this.convertToBaseUnit(value);
        return feetValue / targetUnit.conversionFactor;
    }
    
    @Override
    public String getUnitName() {
    	return this.getUnitName();
    }
}