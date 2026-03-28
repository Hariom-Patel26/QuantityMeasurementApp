package com.app.quantitymeasurement.unit;


public interface IMeasurable {

 
    String getUnitName();

 
    double convertToBaseUnit(double value);


    double convertFromBaseUnit(double baseValue);


    String getMeasurementType();

   
    IMeasurable getUnitInstance(String unitName);


    default boolean supportsArithmetic() {
        return this instanceof SupportsArithmetic;
    }


    default void validateOperationSupport(String operationName) {
        if (!supportsArithmetic()) {
            throw new UnsupportedOperationException(
                getMeasurementType() + " does not support " + operationName);
        }
    }


    default double getConversionFactor() {
        return convertToBaseUnit(1.0);
    }
}
