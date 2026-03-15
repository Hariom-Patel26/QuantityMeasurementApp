package com.apps.quantitymeasurement.units;

import java.util.function.Function;
import com.apps.quantitymeasurement.interfaces.IMeasurable;

public enum TemperatureUnit implements IMeasurable {

	CELSIUS(
		v -> v, 
		v -> v 
	), 
	FAHRENHEIT(
		v -> (v - 32.0) * 5.0 / 9.0,
		v -> v * 9.0 / 5.0 + 32.0
	),
	KELVIN(
		v -> v - 273.15,
		v -> v + 273.15
	);

	private final Function<Double, Double> toBase;
	private final Function<Double, Double> fromBase;

	TemperatureUnit(Function<Double, Double> toBase, Function<Double, Double> fromBase) {
		this.toBase = toBase;
		this.fromBase = fromBase;
	}

	@Override
	public String getUnitName() {
		return this.name();
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
		double base = convertToBaseUnit(value);
		return target.convertFromBaseUnit(base);
	}

	@Override
	public String getMeasurementType() {
		return this.getClass().getSimpleName();
	}

	@Override
	public IMeasurable getUnitInstance(String unitName) {
		for (TemperatureUnit unit : TemperatureUnit.values()) {
			if (unit.getUnitName().equalsIgnoreCase(unitName)) {
				return unit;
			}
		}
		throw new IllegalArgumentException("Invalid temperature unit: " + unitName);
	}
}