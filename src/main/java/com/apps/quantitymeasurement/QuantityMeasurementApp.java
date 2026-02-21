package com.apps.quantitymeasurement;

public class QuantityMeasurementApp {

    public static void main(String[] args) {
    	
    	// UC1 - FeetEquality

        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);

        boolean feetResult = feet1.equals(feet2);

        System.out.println("Input: 1.0 ft and 1.0 ft");
        System.out.println("Output: Equal (" + feetResult + ")");
        
        // UC2 - InchEquality

        Inches inch1 = new Inches(1.0);
        Inches inch2 = new Inches(1.0);

        boolean inchResult = inch1.equals(inch2);

        System.out.println("Input: 1.0 inch and 1.0 inch");
        System.out.println("Output: Equal (" + inchResult + ")");
    }
}