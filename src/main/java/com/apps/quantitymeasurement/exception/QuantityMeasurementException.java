package com.apps.quantitymeasurement.exception;

/**
 * QuantityMeasurementException
 *
 * Custom runtime exception used in the Quantity Measurement
 * application to represent errors that occur during quantity
 * operations such as comparison, conversion, or arithmetic.
 *
 * This exception extends {@link RuntimeException}, allowing
 * it to be thrown without mandatory handling while still
 * enabling centralized error management within the application.

 */
public class QuantityMeasurementException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new QuantityMeasurementException with the specified error message.
	 */
	public QuantityMeasurementException(String message) {
		super(message);
	}

	public QuantityMeasurementException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public static void main(String[] args) {
		try {
			throw new QuantityMeasurementException(
				"This is a test exception for quantity measurement."
			);
		} catch(QuantityMeasurementException ex) {
			System.out.println("Caught QuantityMeasurementException: " + 
								ex.getMessage());
		} 
	}
}