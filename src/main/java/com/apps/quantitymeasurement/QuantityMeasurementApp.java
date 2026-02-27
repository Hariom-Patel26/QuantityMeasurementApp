package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {
	/**
	 * GENERIC: Handles equality check for any Quantity type (Length, Weight, etc.)
	 * Replaces demonstrateLengthEquality and demonstrateWeightEquality.
	 */
	public static <U extends IMeasurable> boolean demonstrateEquality(Quantity<U> q1, Quantity<U> q2) {
		boolean result = q1.equals(q2);
		System.out.println("Input: " + q1 + ".equals(" + q2 + ") -> Output: " + result);
		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateConversion(Quantity<U> quantity, U toUnit) {
		Quantity<U> result = quantity.convertTo(toUnit);
		System.out.println("Input: " + quantity + ".convertTo(" + toUnit + ") -> Output: " + result);
		return result;
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2,
			U targetUnit) {
		Quantity<U> result = q1.add(q2, targetUnit);
		System.out.println("Input: " + q1 + ".add(" + q2 + ", " + targetUnit + ") -> Output: " + result);
		return result;
	}

	public static <U extends IMeasurable> double convert(double value, U source, U target) {
		if (source == null || target == null) {
			throw new IllegalArgumentException("Units cannot be null");
		}
		// Using the constructor's finite check logic
		Quantity<U> sourceLength = new Quantity<U>(value, source);

		// Perform conversion through the instance method to maintain DRY principle
		Quantity<U> result = sourceLength.convertTo(target);
		return result.getValue();
	}

	public static void main(String[] args) {

		System.out.println(" Generic Length Operations ---");

		Quantity<LengthUnit> feet = new Quantity<>(1.0, LengthUnit.FEET);
		Quantity<LengthUnit> inches = new Quantity<>(12.0, LengthUnit.INCHES);

		// Demonstrate Equality
		demonstrateEquality(feet, inches);

		// Demonstrate Conversion
		demonstrateConversion(feet, LengthUnit.INCHES);

		// Demonstrate Addition
		demonstrateAddition(feet, inches, LengthUnit.FEET);

		System.out.println("Generic Weight Operations ---");

		Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
		Quantity<WeightUnit> grams = new Quantity<>(1000.0, WeightUnit.GRAM);

		// Demonstrate Equality
		demonstrateEquality(kg, grams);

		// Demonstrate Conversion
		demonstrateConversion(kg, WeightUnit.GRAM);

		// Demonstrate Addition
		demonstrateAddition(kg, grams, WeightUnit.KILOGRAM);

		System.out.println("Category Incompatibility Check ---");

		Quantity<WeightUnit> oneKg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
		Quantity<LengthUnit> oneFeet = new Quantity<>(1.0, LengthUnit.FEET);

		System.out.println("Input: 1.0 kg equals 1.0 feet -> Output: " + oneKg.equals(oneFeet));

		//   UC 11: Volume Measurement Implementation  
		System.out.println("UC 11: Volume Operations");

		
		Quantity<VolumeUnit> oneLitre = new Quantity<>(1.0, VolumeUnit.LITRE);
		Quantity<VolumeUnit> thousandML = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
		Quantity<VolumeUnit> oneGallon = new Quantity<>(1.0, VolumeUnit.GALLON);

		//  Equality Comparisons
		System.out.println("Input: 1.0 LITRE equals 1000.0 MILLILITRE -> " + oneLitre.equals(thousandML));
		System.out.println("Input: 3.78541 LITRE equals 1.0 GALLON -> "
				+ new Quantity<>(3.78541, VolumeUnit.LITRE).equals(oneGallon));

		//  Unit Conversions
		System.out.println("Input: 1.0 LITRE convertTo MILLILITRE -> " + oneLitre.convertTo(VolumeUnit.MILLILITRE));
		System.out.println("Input: 2.0 GALLON convertTo LITRE -> "
				+ new Quantity<>(2.0, VolumeUnit.GALLON).convertTo(VolumeUnit.LITRE));

		//   Addition Operations
		// 1L + 1000mL = 2L (Implicit unit)
		System.out.println("Input: 1.0 LITRE add 1000.0 MILLILITRE -> " + oneLitre.add(thousandML, VolumeUnit.LITRE));

		// 1.0 GALLON + 3.78541 LITRE (Target GALLON) -> 2.0 GALLON
		System.out.println("Input: 1.0 GALLON add 3.78541 LITRE (Target GALLON) -> "
				+ oneGallon.add(new Quantity<>(3.78541, VolumeUnit.LITRE), VolumeUnit.GALLON));

		//   Category Incompatibility
		Quantity<LengthUnit> oneFoot = new Quantity<>(1.0, LengthUnit.FEET);
		System.out.println("Input: 1.0 LITRE equals 1.0 FOOT -> " + oneLitre.equals(oneFoot));
	}
}