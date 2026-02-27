package com.apps.quantitymeasurement;

public enum VolumeUnit implements IMeasurable {
	LITRE(1.0),MILLILITRE(0.001),GALLON(3.78541);
	
	private final double conversionFactor;
	
	VolumeUnit(double conversionFactor){
		this.conversionFactor=conversionFactor;
	}
	@Override
	public double getConversionFactor() {
		return conversionFactor;
	}

	@Override
	public double convertToBaseUnit(double value) {
		return value * getConversionFactor();
	}

	@Override
	public double convertFromBaseUnit(double baseValue) {
		return baseValue / getConversionFactor();
	}
	 public double convert(double value, VolumeUnit targetUnit) {
	        double baseValue = this.convertToBaseUnit(value);
	        return baseValue / targetUnit.getConversionFactor();
	 }

	@Override
	public String getUnitName() {
		return this.getUnitName();
	}	
}