package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {
	// Create a generic method to demonstrate Length equality check
	public static boolean demonstrateLengthEquality(Length length1, Length length2) {
		System.out.println("Equal (" + length1.compare(length2) + ")");
		return length1.equals(length2);
	}

	// Create a static method to take in method parameters and demonstrate equality check
	public static boolean demonstarteLengthComparison(Length length1, Length length2) {
		System.out.println("Compare (" + length1.compare(length2) + ")");
		return length1.compare(length2);
	}

	// create a method to demonstrate length unit conversion with 3 parameters
	public static Length demonstrateLengthConversion(double value, LengthUnit fromUnit, LengthUnit toUnit) {
		Length base = new Length(value, fromUnit);
		return base.convertTo(toUnit);
	}

	// create a method to demonstrate the length conversion from one QuantityLength
	// instance to another unit
	public static Length demonstrateLengthConversion(Length length, LengthUnit toUnit) {
		return length.convertTo(toUnit);
	}

	// create a method to demonstrate the addition of two length units
	public static Length demonstrateLengthAddition(Length l1, Length l2) {
		return l1.add(l2);
	}

	public static Length demonstrateLengthAddition(Length l1, Length l2, LengthUnit targetUnit) {
		return l1.add(l2, targetUnit);
	}

	public static double convert(double value, LengthUnit source, LengthUnit target) {
		if (source == null || target == null) {
			throw new IllegalArgumentException("Units cannot be null");
		}
		// Using the constructor's finite check logic
		Length sourceLength = new Length(value, source);

		// Perform conversion through the instance method to maintain DRY principle
		Length result = sourceLength.convertTo(target);
		return result.getValue();
	}

	// --- Weight Demonstration Methods ---

	public static boolean demonstrateWeightEquality(Weight w1, Weight w2) {
		return w1.equals(w2);
	}

	public static Weight demonstrateWeightConversion(Weight weight, WeightUnit toUnit) {
		return weight.convertTo(toUnit);
	}

	public static Weight demonstrateWeightAddition(Weight w1, Weight w2) {
		return w1.add(w2);
	}

	public static Weight demonstrateWeightAddition(Weight w1, Weight w2, WeightUnit target) {
		return w1.add(w2, target);
	}

	// Main method
	public static void main(String[] args) {
		// Demonstrate Feet and Inches comparison
		demonstarteLengthComparison(new Length(1, LengthUnit.FEET), new Length(12, LengthUnit.INCHES));
		// Demonstrate Yards and Inches comparison
		demonstarteLengthComparison(new Length(1, LengthUnit.YARDS), new Length(36, LengthUnit.INCHES));

		// Demonstrate Feet and Inches conversion using the value
		System.out.println(demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCHES));
		// Demonstrate YARDS and FEET conversion using the value
		System.out.println(demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET));
		// Demonstrate Inches AND yards conversion using the value
		System.out.println(demonstrateLengthConversion(36.0, LengthUnit.INCHES, LengthUnit.YARDS));
		// Demonstrate CENTIMETETRS and Inches conversion using the value
		System.out.println(demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCHES));
		// Demonstrate Feet and Inches conversion
		System.out.println(demonstrateLengthConversion(new Length(1.0, LengthUnit.FEET), LengthUnit.INCHES));

		// UC 6
		// demonstrate addition of Feet with inches
		System.out.println(
				demonstrateLengthAddition(new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES)));
		// demonstrate addition of Feet to yards
		System.out.println(
				demonstrateLengthAddition(new Length(1.0, LengthUnit.FEET), new Length(2.0, LengthUnit.YARDS)));
		System.out.println("Input: add(1.0 FEET, 2.0 FEET) -> Output: "
				+ demonstrateLengthAddition(new Length(1.0, LengthUnit.FEET), new Length(2.0, LengthUnit.FEET)));

		// Cross-unit addition (Feet + Inches) -> Expected: 2.0 FEET
		System.out.println("Input: add(1.0 FEET, 12.0 INCHES) -> Output: "
				+ demonstrateLengthAddition(new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES)));

		// Cross-unit addition (Inches + Feet) -> Expected: 24.0 INCHES
		System.out.println("Input: add(12.0 INCHES, 1.0 FEET) -> Output: "
				+ demonstrateLengthAddition(new Length(12.0, LengthUnit.INCHES), new Length(1.0, LengthUnit.FEET)));

		// Cross-unit addition (Yards + Feet) -> Expected: 2.0 YARDS
		System.out.println("Input: add(1.0 YARDS, 3.0 FEET) -> Output: "
				+ demonstrateLengthAddition(new Length(1.0, LengthUnit.YARDS), new Length(3.0, LengthUnit.FEET)));

		// Cross-unit addition (Inches + Yards) -> Expected: 72.0 INCHES
		System.out.println("Input: add(36.0 INCHES, 1.0 YARDS) -> Output: "
				+ demonstrateLengthAddition(new Length(36.0, LengthUnit.INCHES), new Length(1.0, LengthUnit.YARDS)));

		// Centimeter + Inch addition -> Expected: ~5.08 CENTIMETERS
		System.out.println("Input: add(2.54 CENTIMETERS, 1.0 INCHES) -> Output: " + demonstrateLengthAddition(
				new Length(2.54, LengthUnit.CENTIMETERS), new Length(1.0, LengthUnit.INCHES)));

		// Identity Element (Adding Zero)
		System.out.println("Input: add(5.0 FEET, 0.0 INCHES) -> Output: "
				+ demonstrateLengthAddition(new Length(5.0, LengthUnit.FEET), new Length(0.0, LengthUnit.INCHES)));

		// Negative Value Addition
		System.out.println("Input: add(5.0 FEET, -2.0 FEET) -> Output: "
				+ demonstrateLengthAddition(new Length(5.0, LengthUnit.FEET), new Length(-2.0, LengthUnit.FEET)));

		// UC 7
		System.out.println("UC 7 implementation");
		System.out.println("Input: add(1.0 FEET, 12.0 INCHES, FEET) -> Output: " + demonstrateLengthAddition(
				new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES), LengthUnit.FEET));

		System.out.println("Input: add(1.0 FEET, 12.0 INCHES, INCHES) -> Output: " + demonstrateLengthAddition(
				new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES), LengthUnit.INCHES));

		System.out.println("Input: add(1.0 FEET, 12.0 INCHES, YARDS) -> Output: " + demonstrateLengthAddition(
				new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES), LengthUnit.YARDS));

		System.out.println("Input: add(1.0 YARDS, 3.0 FEET, YARDS) -> Output: " + demonstrateLengthAddition(
				new Length(1.0, LengthUnit.YARDS), new Length(3.0, LengthUnit.FEET), LengthUnit.YARDS));

		System.out.println("Input: add(36.0 INCHES, 1.0 YARDS, FEET) -> Output: " + demonstrateLengthAddition(
				new Length(36.0, LengthUnit.INCHES), new Length(1.0, LengthUnit.YARDS), LengthUnit.FEET));

		System.out.println("Input: add(2.54 CENTIMETERS, 1.0 INCHES, CENTIMETERS) -> Output: "
				+ demonstrateLengthAddition(new Length(2.54, LengthUnit.CENTIMETERS),
						new Length(1.0, LengthUnit.INCHES), LengthUnit.CENTIMETERS));

		System.out.println("Input: add(5.0 FEET, 0.0 INCHES, YARDS) -> Output: " + demonstrateLengthAddition(
				new Length(5.0, LengthUnit.FEET), new Length(0.0, LengthUnit.INCHES), LengthUnit.YARDS));

		System.out.println("Input: add(5.0 FEET, -2.0 FEET, INCHES) -> Output: " + demonstrateLengthAddition(
				new Length(5.0, LengthUnit.FEET), new Length(-2.0, LengthUnit.FEET), LengthUnit.INCHES));

		// --- UC8: Refactored Design Outputs ---
		System.out.println("\n--- UC 8 implementation ---");

		// Quantity(1.0, FEET).convertTo(INCHES)
		System.out.println("Input: Quantity(1.0, FEET).convertTo(INCHES) -> Output: "
				+ demonstrateLengthConversion(new Length(1.0, LengthUnit.FEET), LengthUnit.INCHES));

		// Quantity(1.0, FEET).add(Quantity(12.0, INCHES), FEET)
		System.out.println(
				"Input: Quantity(1.0, FEET).add(Quantity(12.0, INCHES), FEET) -> Output: " + demonstrateLengthAddition(
						new Length(1.0, LengthUnit.FEET), new Length(12.0, LengthUnit.INCHES), LengthUnit.FEET));

		// Quantity(36.0, INCHES).equals(Quantity(1.0, YARDS))
		Length thirtySixInches = new Length(36.0, LengthUnit.INCHES);
		Length oneYard = new Length(1.0, LengthUnit.YARDS);
		System.out.println("Input: Quantity(36.0, INCHES).equals(Quantity(1.0, YARDS)) -> Output: "
				+ thirtySixInches.equals(oneYard));

		// Quantity(1.0, YARDS).add(Quantity(3.0, FEET), YARDS)
		System.out.println(
				"Input: Quantity(1.0, YARDS).add(Quantity(3.0, FEET), YARDS) -> Output: " + demonstrateLengthAddition(
						new Length(1.0, LengthUnit.YARDS), new Length(3.0, LengthUnit.FEET), LengthUnit.YARDS));

		// Quantity(2.54, CENTIMETERS).convertTo(INCHES)
		System.out.println("Input: Quantity(2.54, CENTIMETERS).convertTo(INCHES) -> Output: "
				+ demonstrateLengthConversion(new Length(2.54, LengthUnit.CENTIMETERS), LengthUnit.INCHES));

		// Quantity(5.0, FEET).add(Quantity(0.0, INCHES), FEET)
		System.out.println(
				"Input: Quantity(5.0, FEET).add(Quantity(0.0, INCHES), FEET) -> Output: " + demonstrateLengthAddition(
						new Length(5.0, LengthUnit.FEET), new Length(0.0, LengthUnit.INCHES), LengthUnit.FEET));

		// LengthUnit.FEET.convertToBaseUnit(12.0)
		System.out.println(
				"Input: LengthUnit.FEET.convertToBaseUnit(12.0) -> Output: " + LengthUnit.FEET.convertToBaseUnit(12.0));

		// LengthUnit.INCHES.convertToBaseUnit(12.0)
		System.out.println("Input: LengthUnit.INCHES.convertToBaseUnit(12.0) -> Output: "
				+ LengthUnit.INCHES.convertToBaseUnit(12.0));
		
		
		System.out.println("--- UC 9:  Weight Measurement Implementation ---");

        //  Equality Comparisons 
        System.out.println("Input: Quantity(1.0, KILOGRAM).equals(Quantity(1.0, KILOGRAM)) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).equals(new Weight(1.0, WeightUnit.KILOGRAM)));
        
        System.out.println("Input: Quantity(1.0, KILOGRAM).equals(Quantity(1000.0, GRAM)) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).equals(new Weight(1000.0, WeightUnit.GRAM)));
        
        System.out.println("Input: Quantity(2.0, POUND).equals(Quantity(2.0, POUND)) -> Output: " + 
            new Weight(2.0, WeightUnit.POUND).equals(new Weight(2.0, WeightUnit.POUND)));
        
        System.out.println("Input: Quantity(1.0, KILOGRAM).equals(Quantity(2.20462, POUND)) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).equals(new Weight(2.20462, WeightUnit.POUND)));

        //   Unit Conversions 
        System.out.println("Input: Quantity(1.0, KILOGRAM).convertTo(GRAM) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).convertTo(WeightUnit.GRAM));
        
        System.out.println("Input: Quantity(2.0, POUND).convertTo(KILOGRAM) -> Output: " + 
            new Weight(2.0, WeightUnit.POUND).convertTo(WeightUnit.KILOGRAM));

        //   Addition Operations (Implicit) 
        System.out.println("Input: Quantity(1.0, KILOGRAM).add(Quantity(2.0, KILOGRAM)) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).add(new Weight(2.0, WeightUnit.KILOGRAM)));
        
        System.out.println("Input: Quantity(1.0, KILOGRAM).add(Quantity(1000.0, GRAM)) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).add(new Weight(1000.0, WeightUnit.GRAM)));

        //  Addition Operations (Explicit) 
        System.out.println("Input: Quantity(1.0, KILOGRAM).add(Quantity(1000.0, GRAM), GRAM) -> Output: " + 
            new Weight(1.0, WeightUnit.KILOGRAM).add(new Weight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM));
        
        System.out.println("Input: Quantity(1.0, POUND).add(Quantity(453.592, GRAM), POUND) -> Output: " + 
            new Weight(1.0, WeightUnit.POUND).add(new Weight(453.592, WeightUnit.GRAM), WeightUnit.POUND));

        // Category Incompatibility 
        Weight weight = new Weight(1.0, WeightUnit.KILOGRAM);
        Length length = new Length(1.0, LengthUnit.FEET);
        System.out.println("Input: Quantity(1.0, KILOGRAM).equals(Quantity(1.0, FOOT)) -> Output: " + 
            weight.equals(length));
	}
}