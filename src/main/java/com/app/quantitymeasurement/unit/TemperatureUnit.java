package com.app.quantitymeasurement.unit;

import java.util.function.Function;


public enum TemperatureUnit implements IMeasurable {

    CELSIUS(
        v -> v,                          // Celsius → Celsius (identity)
        v -> v                           // Celsius → Celsius (identity)
    ),
    FAHRENHEIT(
        v -> (v - 32.0) * 5.0 / 9.0,    // Fahrenheit → Celsius
        v -> v * 9.0 / 5.0 + 32.0       // Celsius → Fahrenheit
    ),
    KELVIN(
        v -> v - 273.15,                 // Kelvin → Celsius
        v -> v + 273.15                  // Celsius → Kelvin
    );

    /** Converts a value in this unit to the base unit (CELSIUS). */
    private final Function<Double, Double> toBase;

    /** Converts a CELSIUS value to this unit. */
    private final Function<Double, Double> fromBase;

    TemperatureUnit(Function<Double, Double> toBase, Function<Double, Double> fromBase) {
        this.toBase   = toBase;
        this.fromBase = fromBase;
    }


    @Override
    public double convertToBaseUnit(double value) {
        return toBase.apply(value);
    }


    @Override
    public double convertFromBaseUnit(double baseValue) {
        return fromBase.apply(baseValue);
    }


    public double convertTo(double value, TemperatureUnit target) {
        return target.convertFromBaseUnit(convertToBaseUnit(value));
    }

  
    @Override
    public String getUnitName() { return this.name(); }

   
    @Override
    public String getMeasurementType() { return this.getClass().getSimpleName(); }

 
    @Override
    public IMeasurable getUnitInstance(String unitName) {
        for (TemperatureUnit unit : TemperatureUnit.values()) {
            if (unit.getUnitName().equalsIgnoreCase(unitName)) return unit;
        }
        throw new IllegalArgumentException("Invalid temperature unit: " + unitName);
    }
}
