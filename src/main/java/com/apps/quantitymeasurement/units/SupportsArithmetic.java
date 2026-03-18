package com.apps.quantitymeasurement.units;

/**
 * SupportsArithmetic
 *
 * Marker interface used to indicate that a measurement unit
 * supports arithmetic operations.
 *
 * A marker interface does not declare any methods. Instead,
 * it acts as a tag that can be checked at runtime to determine
 * whether a particular unit allows arithmetic operations.
 *
 * In the Quantity Measurement system, units that implement
 * this interface can participate in operations such as:
 * <ul>
 * <li>Addition</li>
 * <li>Subtraction</li>
 * <li>Division</li>
 * </ul>
 *
 * Units that do not implement this interface are treated
 * as non-arithmetic units. For example, temperature units
 * generally do not support arithmetic operations.
 *
 * The service layer can verify arithmetic capability using
 * the {@code instanceof SupportsArithmetic} check.
 *
 * Example:
 * LengthUnit, WeightUnit, and VolumeUnit implement this
 * marker interface.
 */
public interface SupportsArithmetic {
}