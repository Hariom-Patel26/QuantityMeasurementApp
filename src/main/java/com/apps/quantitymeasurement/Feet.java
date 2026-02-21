package com.apps.quantitymeasurement;

import java.util.Objects;

public class Feet {

    // Encapsulation + Immutability
    private final double value;

    // Constructor
    public Feet(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    // Override equals() method
    @Override
    public boolean equals(Object obj) {

        // Same reference
        if (this == obj) {
            return true;
        }

        // Null check
        if (obj == null) {
            return false;
        }

        // Type check
        if (getClass() != obj.getClass()) {
            return false;
        }

        // Safe casting
        Feet other = (Feet) obj;

        // Floating point comparison
        return Double.compare(this.value, other.value) == 0;
    }

    //override hashCode
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}