package com.apps.quantitymeasurement;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {
	CELSIUS(val -> val, val-> val),
	FAHRENHEIT(val -> (val - 32) * 5/9, base -> (base * 9/5) + 32);

	private final Function<Double, Double> toBase;
	private final Function<Double, Double> fromBase;

	TemperatureUnit(Function<Double, Double> toBase, Function<Double, Double> fromBase) {
        this.toBase = toBase;
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

	@Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException("Temperature does not support " + operation);
    }

	@Override
    public boolean supportsArithmetic() {
        return false;
    } 
}