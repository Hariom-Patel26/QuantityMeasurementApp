package com.apps.quantitymeasurement;

@FunctionalInterface
interface SupportsArithmetic {
	boolean isSupported();
}

public interface IMeasurable {
	SupportsArithmetic supportsArithmetic = () -> true;
//	double getConversionFactor();

	double convertToBaseUnit(double value);

	double convertFromBaseUnit(double baseValue);

//	String getUnitName();

	default boolean supportsArithmetic() {
		return supportsArithmetic.isSupported();
	}

	default void validateOperationSupport(String operation) {
		if (!supportsArithmetic()) {
			throw new UnsupportedOperationException(operation + " is not supported for this unit category.");
		}
	}
}