package com.apps.quantitymeasurement;

public class Length {
	private double value;
	private LengthUnit unit;

	public enum LengthUnit {
//		FEET(conversionFactor: 12.0),
		FEET(12.0), INCHES(1.0), YARDS(36.0), CENTIMETERS(0.393701);

		private final double conversionFactor;

		LengthUnit(double conversionFactor) {
			this.conversionFactor = conversionFactor;
		}

		public double getConversionFactor() {
			return conversionFactor;
		}
	}

	public Length(double value, LengthUnit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("unit cannot be null");
		}
		if (!Double.isFinite(value)) {
			throw new IllegalArgumentException("Value must be finite");
		}
		this.value = value;
		this.unit = unit;
	}

	public double getValue() {
		return value;
	}

	public LengthUnit getUnit() {
		return unit;
	}

	double convertToBaseUnit() {
		return this.value * this.unit.getConversionFactor();
//		double convertedValue = this.value * this.unit.getConversionFactor();
//		return Math.round(convertedValue*100)/100;
	}

	public boolean compare(Length thatLength) {
		if (thatLength == null) {
			return false;
		}
		return Double.compare(this.convertToBaseUnit(), thatLength.convertToBaseUnit()) == 0;
	}

	// Override equals method
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		Length that = (Length) o;

		// Defensive check in case a null unit somehow bypassed the constructor
		if (this.unit == null || that.unit == null)
			return false;

		return Math.abs(this.convertToBaseUnit() - that.convertToBaseUnit()) < 0.00001;
	}

	// conversion logic UC5
	public Length convertTo(LengthUnit targetUnit) {
		if (targetUnit == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		double baseValue = this.convertToBaseUnit();
		double convertValue = baseValue / targetUnit.getConversionFactor();
		// double convertValue= Math.round((baseValue /
		// targetUnit.getConversionFactor())*100)/100;
		return new Length(convertValue, targetUnit);
	}

	@Override
	public String toString() {
		return String.format("%.2f %s", value, unit);
	}

	// UC6: Implicit Target
	public Length add(Length that) {
		return add(that, this.unit); // Reuses the overloaded method
	}

	// UC7: Adds two measurements and returns the result in a specified target unit.
	// * Uses a private helper to maintain the DRY principle.
	public Length add(Length that, LengthUnit targetUnit) {
		// Ensuring non-nullity and finite values
		if (that == null || targetUnit == null) {
			throw new IllegalArgumentException("Operand and target unit cannot be null");
		}
		// Explicit finite check for the current object and the operand
		if (!Double.isFinite(this.value) || !Double.isFinite(that.value)) {
			throw new IllegalArgumentException("Measurement values must be finite");
		}
		return addAndConvert(that, targetUnit);
	}

	/**
	 * UC 7 Private utility method to centralize addition logic. Ensures consistent
	 * precision and immutability.
	 */
	private Length addAndConvert(Length length, LengthUnit targetUnit) {
		double sumInBaseUnit = this.convertToBaseUnit() + length.convertToBaseUnit();

		double finalValue = convertFromBaseToTargetUnit(sumInBaseUnit, targetUnit);
		return new Length(finalValue, targetUnit);
	}

	private double convertFromBaseToTargetUnit(double lengthInInches, LengthUnit target) {
		return lengthInInches / target.getConversionFactor();
	}

	// main method for standalone testing
	public static void main(String[] args) {
		Length l1 = new Length(1.0, LengthUnit.FEET);
		Length l2 = new Length(12.0, LengthUnit.INCHES);
		System.out.println("Are lengths equal ? " + l1.equals(l2));

		Length length3 = new Length(1, LengthUnit.YARDS);
		Length length4 = new Length(36, LengthUnit.INCHES);
		System.out.println("Are lengths equals? " + length3.equals(length4)); // Should print true;

		Length length5 = new Length(100, LengthUnit.CENTIMETERS);
		Length length6 = new Length(39.3701, LengthUnit.INCHES);
		System.out.println("Are lengths equals? " + length5.equals(length6)); // Should print true;

		// UC7: Explicitly requesting YARDS
		System.out.println("Result in YARDS: " + l1.add(l2, LengthUnit.YARDS));
		// UC7: Explicitly requesting INCHES
		System.out.println("Result in INCHES: " + l1.add(l2, LengthUnit.INCHES));
	}

}