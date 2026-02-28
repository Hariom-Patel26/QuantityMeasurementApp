package com.apps.quantitymeasurement;

public class Weight {
	private double value;
	private WeightUnit unit;

	public Weight(double value, WeightUnit unit) {
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

	public WeightUnit getUnit() {
		return unit;
	}

	public boolean compare(Weight that) {
		if (that == null) {
			return false;
		}
		return Double.compare(this.unit.convertToBaseUnit(this.value), that.unit.convertToBaseUnit(that.value)) == 0;
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

		Weight that = (Weight) o;

		// Defensive check in case a null unit somehow bypassed the constructor
		if (this.unit == null || that.unit == null)
			return false;

		return Math.abs(this.unit.convertToBaseUnit(this.value) - that.unit.convertToBaseUnit(that.value)) < 0.00001;
	}

	// conversion logic UC5
	public Weight convertTo(WeightUnit targetUnit) {
		if (targetUnit == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		double baseValue = this.unit.convertToBaseUnit(this.value);
		double convertValue = baseValue / targetUnit.getConversionFactor();
		// double convertValue= Math.round((baseValue /
		// targetUnit.getConversionFactor())*100)/100;
		return new Weight(convertValue, targetUnit);
	}

	@Override
	public String toString() {
		return String.format("%.2f %s", value, unit);
	}

	// UC6: Implicit Target
	public Weight add(Weight that) {
		return add(that, this.unit); // Reuses the overloaded method
	}

	// UC7: Adds two measurements and returns the result in a specified target unit.
	//  Uses a private helper to maintain the DRY principle.
	public Weight add(Weight that, WeightUnit targetUnit) {
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

// UC 7 Private utility method to centralize addition logic. Ensures consistent precision and immutability.

	private Weight addAndConvert(Weight weight, WeightUnit targetUnit) {
		double sumInBaseUnit = this.unit.convertToBaseUnit(this.value) + weight.unit.convertToBaseUnit(weight.value);

		double finalValue = convertFromBaseToTargetUnit(sumInBaseUnit, targetUnit);
		return new Weight(finalValue, targetUnit);
	}

	private double convertFromBaseToTargetUnit(double weightInKGs, WeightUnit target) {
		return weightInKGs / target.getConversionFactor();
	}

	public static void main(String[] args) {
		Weight w1 = new Weight(1.0, WeightUnit.KILOGRAM);
		Weight w2 = new Weight(1000.0, WeightUnit.GRAM);

		System.out.println("Are w1 and w2 equal : " + w1.equals(w2));
	}
}