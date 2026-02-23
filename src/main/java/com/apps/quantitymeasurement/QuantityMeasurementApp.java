package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.Length.LengthUnit;

public class QuantityMeasurementApp {
	// Create a generic method to demonstrate Length equality check
	public static boolean demonstrateLengthEquality(Length length1, Length length2) {
		System.out.println("Equal (" + length1.compare(length2) + ")");
		return length1.equals(length2);
	}

	// Create a static method to take in method parameters and demonstrate equality
	// check
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
	public static Length demonstrateLengthConversion(Length length, Length.LengthUnit toUnit) {
		return length.convertTo(toUnit);
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
		System.out.println(demonstrateLengthConversion(new Length(1.0,LengthUnit.FEET), LengthUnit.INCHES));

	}
}