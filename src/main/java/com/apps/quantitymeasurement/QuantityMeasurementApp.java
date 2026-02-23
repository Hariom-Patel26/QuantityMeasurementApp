package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.Length.LengthUnit;

public class QuantityMeasurementApp {
	
	// UC1 - FeetEquality
	public static void demonstrateFeetEquality() {
		
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        boolean feetResult = feet1.equals(feet2);

        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + feetResult + ")");
	}
	
    // UC2 - InchEquality
	public static void demonstrateInchesEquality() {
		
		Inches inch1 = new Inches(1.0);
        Inches inch2 = new Inches(1.0);

        boolean inchResult = inch1.equals(inch2);

        System.out.println("Input: 1.0 inch and 1.0 inch");
        System.out.println("Output: Equal (" + inchResult + ")");
		
	}
	//UC3 - Generic method
	 public static void demonstrateLengthEquality() {

	        Length l1 = new Length(1.0, LengthUnit.FEET);
	        Length l2 = new Length(12.0, LengthUnit.INCHES);

	        System.out.println("Input: Length(1.0, FEET) and Length(12.0, INCHES)");
	        System.out.println("Output: Equal -> " + l1.equals(l2));
	    }

    public static void main(String[] args) {
    	
		//demonstrateFeetEquality();
		//demonstrateInchesEquality();  
		demonstrateLengthEquality();
    }
}