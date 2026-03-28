package com.app.quantitymeasurement.entity;

import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.unit.IMeasurable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "quantity_measurement")
@Data
@EqualsAndHashCode(of = {"thisValue", "thisUnit", "thatValue", "thatUnit", "operation"})
@NoArgsConstructor
public class QuantityMeasurementEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "this_value")
    private Double thisValue;

    @Column(name = "this_unit")
    private String thisUnit;

    @Column(name = "this_measurement_type")
    private String thisMeasurementType;



    @Column(name = "that_value")
    private Double thatValue;

    @Column(name = "that_unit")
    private String thatUnit;

    @Column(name = "that_measurement_type")
    private String thatMeasurementType;

 
    @Column(name = "operation")
    private String operation;

   
    @Column(name = "result_value")
    private Double resultValue;

   
    @Column(name = "result_unit")
    private String resultUnit;

  
    @Column(name = "result_measurement_type")
    private String resultMeasurementType;

   
    @Column(name = "result_string")
    private String resultString;


    @Column(name = "is_error")
    private boolean error;


    @Column(name = "error_message", length = 1000)
    private String errorMessage;

  
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }




    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation,
            String result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultString = result;
    }


    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation,
            QuantityModel<IMeasurable> result) {
        this(thisQuantity, thatQuantity, operation);
        this.resultValue            = result.getValue();
        this.resultUnit             = result.getUnit().getUnitName();
        this.resultMeasurementType  = result.getUnit().getMeasurementType();
    }


    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation,
            String errorMessage,
            boolean isError) {
        this(thisQuantity, thatQuantity, operation);
        this.errorMessage = errorMessage;
        this.error        = isError;
    }


    public QuantityMeasurementEntity(
            QuantityModel<IMeasurable> thisQuantity,
            QuantityModel<IMeasurable> thatQuantity,
            String operation) {
        if (thisQuantity == null || thatQuantity == null) {
            throw new IllegalArgumentException("Quantities cannot be null");
        }
        this.thisValue           = thisQuantity.getValue();
        this.thisUnit            = thisQuantity.getUnit().getUnitName();
        this.thisMeasurementType = thisQuantity.getUnit().getMeasurementType();
        this.thatValue           = thatQuantity.getValue();
        this.thatUnit            = thatQuantity.getUnit().getUnitName();
        this.thatMeasurementType = thatQuantity.getUnit().getMeasurementType();
        this.operation           = operation;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(error ? "[ERROR] " : "[SUCCESS] ").append("operation=").append(operation);
        sb.append(", operand1=").append(thisValue).append(" ").append(thisUnit);
        sb.append(", operand2=").append(thatValue).append(" ").append(thatUnit);
        if (error) {
            sb.append(", message=").append(errorMessage);
        } else if (resultString != null && !resultString.isEmpty()) {
            sb.append(", result=").append(resultString);
        } else {
            sb.append(", result=").append(resultValue).append(" ").append(resultUnit);
        }
        return sb.toString();
    }
}
