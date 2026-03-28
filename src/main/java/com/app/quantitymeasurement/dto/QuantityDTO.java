package com.app.quantitymeasurement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class QuantityDTO {


    public interface IMeasurableUnit {
        String getUnitName();
        String getMeasurementType();
    }


    public enum LengthUnit implements IMeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS;

        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }


    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, MILLILITRE, GALLON;

        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

 
    public enum WeightUnit implements IMeasurableUnit {
        KILOGRAM, GRAM, POUND;

        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }

 
    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;

        @Override public String getUnitName()        { return this.name(); }
        @Override public String getMeasurementType() { return this.getClass().getSimpleName(); }
    }


    @NotNull(message = "Value must not be null")
    private Double value;

   
    @NotEmpty(message = "Unit must not be empty")
    private String unit;

 
    @NotEmpty(message = "Measurement type must not be empty")
    @Pattern(
        regexp = "LengthUnit|VolumeUnit|WeightUnit|TemperatureUnit",
        message = "Measurement type must be one of: LengthUnit, VolumeUnit, WeightUnit, TemperatureUnit"
    )
    private String measurementType;

    
    public QuantityDTO(double value, IMeasurableUnit unit) {
        this.value           = value;
        this.unit            = unit.getUnitName();
        this.measurementType = unit.getMeasurementType();
    }

 
    public QuantityDTO(double value, String unit, String measurementType) {
        this.value           = value;
        this.unit            = unit;
        this.measurementType = measurementType;
    }


    public double getValue() {
        return value == null ? 0.0 : value;
    }


    @jakarta.validation.constraints.AssertTrue(
        message = "Unit must be valid for the specified measurement type")
    public boolean isUnitValidForMeasurementType() {
        if (unit == null || measurementType == null) return true;
        try {
            switch (measurementType) {
                case "LengthUnit":      LengthUnit.valueOf(unit);      break;
                case "VolumeUnit":      VolumeUnit.valueOf(unit);      break;
                case "WeightUnit":      WeightUnit.valueOf(unit);      break;
                case "TemperatureUnit": TemperatureUnit.valueOf(unit); break;
                default: return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    @Override
    public String toString() {
        return String.format("%s %s",
            Double.toString(value == null ? 0.0 : value).replaceAll("\\.0+$", ""),
            unit);
    }
}
