package com.apps.quantitymeasurement;

public enum VolumeUnit implements IMeasurable {
	LITRE(1.0),MILLILITRE(0.001),GALLON(3.78541);
	
	final double conversionFactor;
	
	VolumeUnit(double conversionFactor){
		this.conversionFactor=conversionFactor;
	}
	public double getConversionFactor() {
		return conversionFactor;
	}

	@Override
	public double convertToBaseUnit(double value) {
		// TODO Auto-generated method stub
		return value * conversionFactor;
	}

	@Override
	public double convertFromBaseUnit(double baseValue) {
		// TODO Auto-generated method stub
		return baseValue / conversionFactor;
	}
	 public double convert(double value, VolumeUnit targetUnit) {
	        double baseValue = this.convertToBaseUnit(value);
	        return baseValue / targetUnit.conversionFactor;
	 }

//	@Override
//	public String getUnitName() {
//		// TODO Auto-generated method stub
//		return this.getUnitName();
//	}	
}