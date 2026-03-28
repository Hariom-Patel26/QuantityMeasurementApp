package com.app.quantitymeasurement.unit;


public enum VolumeUnit implements IMeasurable, SupportsArithmetic {

    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.785412);

    /** Multiplier used to convert a value in this unit to the base unit (LITRE). */
    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

 
    @Override
    public double convertToBaseUnit(double value) {
        return Math.round(value * conversionFactor * 1_000_000.0) / 1_000_000.0;
    }


    @Override
    public double convertFromBaseUnit(double baseValue) {
        return Math.round(baseValue / conversionFactor * 1_000_000.0) / 1_000_000.0;
    }

    /** @return the enum constant name as the unit identifier */
    @Override
    public String getUnitName() { return name(); }

    /** @return {@code "VolumeUnit"} */
    @Override
    public String getMeasurementType() { return this.getClass().getSimpleName(); }

 
    @Override
    public IMeasurable getUnitInstance(String unitName) {
        for (VolumeUnit unit : VolumeUnit.values()) {
            if (unit.getUnitName().equalsIgnoreCase(unitName)) return unit;
        }
        throw new IllegalArgumentException("Invalid volume unit: " + unitName);
    }
}
