package com.apps.quantitymeasurement.model;

import com.apps.quantitymeasurement.interfaces.IMeasurable;

/**
 * QuantityModel – Generic POJO model class for representing a quantity with its associated measurable unit.
 */
public class QuantityModel<U extends IMeasurable> {

	private final Double value;
	private final U unit;

	/**
	 * Constructs a QuantityModel with the specified value
	 * and measurable unit.
	 *
	 * @param value numeric quantity value
	 * @param unit measurable unit associated with the value
	 */
	public QuantityModel(double value, U unit) {
		if (unit == null) {
			throw new IllegalArgumentException("Unit cannot be null");
		}
		if (!Double.isFinite(value)) {
			throw new IllegalArgumentException("Value must be finite");
		}
		this.value = value;
		this.unit = unit;
	}

    /**
     * Returns the numeric value of the quantity.
     *
     * @return quantity value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the measurable unit associated with the quantity.
     *
     * @return measurable unit
     */
    public U getUnit() {
        return unit;
    }

    /**
     * Returns the formatted string representation
     * of the quantity model.
     *
     * Example:
     * 5 FEET
     * 10 KILOGRAM
     *
     * @return formatted quantity string
     */
    @Override
    public String toString() {
        return String.format("%s %s", Double.toString(value).replace("\\.0+$", ""), unit.getUnitName());
    }

    /**
     * Main method for testing the functionality
     * of the QuantityModel class.
     *
     * Demonstrates creation of quantity models
     * for different measurement categories and
     * verifies validation behavior.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        System.out.println("---- Testing QuantityModel ----");

        com.apps.quantitymeasurement.units.LengthUnit feet =
                com.apps.quantitymeasurement.units.LengthUnit.FEET;

        QuantityModel<com.apps.quantitymeasurement.units.LengthUnit> length =
                new QuantityModel<>(5, feet);

        System.out.println("Length Model : " + length);

        com.apps.quantitymeasurement.units.WeightUnit kg =
                com.apps.quantitymeasurement.units.WeightUnit.KILOGRAM;

        QuantityModel<com.apps.quantitymeasurement.units.WeightUnit> weight =
                new QuantityModel<>(10, kg);

        System.out.println("Weight Model : " + weight);

        com.apps.quantitymeasurement.units.VolumeUnit litre =
                com.apps.quantitymeasurement.units.VolumeUnit.LITRE;

        QuantityModel<com.apps.quantitymeasurement.units.VolumeUnit> volume =
                new QuantityModel<>(3.5, litre);

        System.out.println("Volume Model : " + volume);

        com.apps.quantitymeasurement.units.TemperatureUnit celsius =
                com.apps.quantitymeasurement.units.TemperatureUnit.CELSIUS;

        QuantityModel<com.apps.quantitymeasurement.units.TemperatureUnit> temp =
                new QuantityModel<>(25, celsius);

        System.out.println("Temperature Model : " + temp);

        System.out.println("\nTesting getters:");

        System.out.println("Value : " + length.getValue());
        System.out.println("Unit  : " + length.getUnit().getUnitName());

        System.out.println("\nTesting exception handling:");

        try {
            new QuantityModel<>(10, null);
        } catch (Exception e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        try {
            new QuantityModel<>(Double.POSITIVE_INFINITY, feet);
        } catch (Exception e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        System.out.println("---- QuantityModel Test Completed ----");
    }
}