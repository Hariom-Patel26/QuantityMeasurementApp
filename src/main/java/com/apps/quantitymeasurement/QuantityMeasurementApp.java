package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import com.apps.quantitymeasurement.dto.QuantityDTO;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;


public class QuantityMeasurementApp {
	
	private static QuantityMeasurementApp instance;
	
	public QuantityMeasurementController controller;
	
	public IQuantityMeasurementRepository repository;
	

	private QuantityMeasurementApp() {
		this.repository = QuantityMeasurementCacheRepository.getInstance();
		QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(
			this.repository
		);
		this.controller = new QuantityMeasurementController(service);
	}
	
	/**
	 * Returns the singleton instance of the application.
	 *
	 * @return application instance
	 */
	public static QuantityMeasurementApp getInstance() {
		if(instance == null) {
			instance = new QuantityMeasurementApp();
		}
		return instance;
	}
 	
	/**
	 * Entry point of the Quantity Measurement Application.
	 *
	 * Demonstrates execution of different measurement operations
	 * including comparison, conversion, arithmetic calculations,
	 * and retrieval of stored measurement records.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
 
	    System.out.println("**** Quantity Measurement Application Started ****\n");
 
	    QuantityMeasurementApp app = QuantityMeasurementApp.getInstance();
	    QuantityMeasurementController controller = app.controller;
 
	    /**
	     * Example 1: Length Equality Demonstration 
	     */
	    
	    QuantityDTO quantity1 = new QuantityDTO(
	        2,
	        QuantityDTO.LengthUnit.FEET.getUnitName(),
	        QuantityDTO.LengthUnit.FEET.getMeasurementType()
	    );
 
	    QuantityDTO quantity2 = new QuantityDTO(
	        24,
	        QuantityDTO.LengthUnit.INCHES.getUnitName(),
	        QuantityDTO.LengthUnit.INCHES.getMeasurementType()
	    );
 
	    controller.demonstrateComparison(quantity1, quantity2);
 
	    System.out.println();
 
	    /**
	     * Example 2: Temperature Conversion
	     */
	    
	    QuantityDTO temp1 = new QuantityDTO(
	        0,
	        QuantityDTO.TemperatureUnit.CELSIUS.getUnitName(),
	        QuantityDTO.TemperatureUnit.CELSIUS.getMeasurementType()
	    );
 
	    QuantityDTO temp2 = new QuantityDTO(
	        0,
	        QuantityDTO.TemperatureUnit.FAHRENHEIT.getUnitName(),
	        QuantityDTO.TemperatureUnit.FAHRENHEIT.getMeasurementType()
	    );
 
	    controller.demonstrateConversion(temp1, temp2);
 
	    System.out.println();
 
	    /**
	     * Example 3: Temperature Addition Attempt — error expected
	     */
	    
	    QuantityDTO tempTarget = new QuantityDTO(
	        0,
	        QuantityDTO.TemperatureUnit.CELSIUS.getUnitName(),
	        QuantityDTO.TemperatureUnit.CELSIUS.getMeasurementType()
	    );
 
	    controller.demonstrateAddition(temp1, temp2, tempTarget);
 
	    System.out.println();
 
	    /**
	     * Example 4: Cross-Category Operation Prevention — error expected
	     */
	    
	    QuantityDTO weightQuantity = new QuantityDTO(
	        10,
	        QuantityDTO.WeightUnit.KILOGRAM.getUnitName(),
	        QuantityDTO.WeightUnit.KILOGRAM.getMeasurementType()
	    );
 
	    controller.demonstrateAddition(quantity1, weightQuantity);
 
	    System.out.println();
 
	    
	    /**
	     * Full operation suite — length
	     */
 
	    QuantityDTO yardsTarget = new QuantityDTO(
	        0,
	        QuantityDTO.LengthUnit.YARDS.getUnitName(),
	        QuantityDTO.LengthUnit.YARDS.getMeasurementType()
	    );
 
	    controller.demonstrateConversion(quantity2, yardsTarget);
	    System.out.println();
 
	    controller.demonstrateAddition(quantity1, quantity2);
	    System.out.println();
 
	    controller.demonstrateAddition(quantity1, quantity2, yardsTarget);
	    System.out.println();
 
	    controller.demonstrateSubtraction(quantity1, quantity2);
	    System.out.println();
 
	    controller.demonstrateDivision(quantity1, quantity2);
	    System.out.println();

	    /**
	     * Stored measurements
	     */
 
	    System.out.println("---- Stored Measurements ----");
 
	    app.repository
	        .getAllMeasurements()
	        .forEach(System.out::println);
 
	    System.out.println("\n**** Quantity Measurement Application Stopped ****");
	}
}