package com.apps.quantitymeasurement; 

public class QuantityMeasurementApp {
	
    /*
      GENERIC: Handles equality check for any Quantity type (Length, Weight, etc.)
      Replaces demonstrateLengthEquality and demonstrateWeightEquality.
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
    
    public static <U extends IMeasurable> Quantity<U> demonstrateAddition(Quantity<U> q1, Quantity<U> q2, U targetUnit) {
        Quantity<U> result = q1.add(q2, targetUnit);
        System.out.println("Input: " + q1 + ".add(" + q2 + ", " + targetUnit + ") -> Output: " + result);
        return result;
    }

	public static <U extends IMeasurable>  double convert(double value, U source, U target) {
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
    }
}