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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

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

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQ, QuantityModel<IMeasurable> thatQ, String op, String result) {
        this(thisQ, thatQ, op);
        this.resultString = result;
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQ, QuantityModel<IMeasurable> thatQ, String op, QuantityModel<IMeasurable> result) {
        this(thisQ, thatQ, op);
        this.resultValue = result.getValue();
        this.resultUnit = result.getUnit().getUnitName();
        this.resultMeasurementType = result.getUnit().getMeasurementType();
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQ, QuantityModel<IMeasurable> thatQ, String op, String errorMessage, boolean isError) {
        this(thisQ, thatQ, op);
        this.errorMessage = errorMessage;
        this.error = isError;
    }

    public QuantityMeasurementEntity(QuantityModel<IMeasurable> thisQ, QuantityModel<IMeasurable> thatQ, String op) {
        if (thisQ == null || thatQ == null) throw new IllegalArgumentException("Quantities cannot be null");
        this.thisValue = thisQ.getValue();
        this.thisUnit = thisQ.getUnit().getUnitName();
        this.thisMeasurementType = thisQ.getUnit().getMeasurementType();
        this.thatValue = thatQ.getValue();
        this.thatUnit = thatQ.getUnit().getUnitName();
        this.thatMeasurementType = thatQ.getUnit().getMeasurementType();
        this.operation = op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(error ? "[ERROR] " : "[SUCCESS] ").append("op=").append(operation);
        sb.append(", q1=").append(thisValue).append(" ").append(thisUnit);
        sb.append(", q2=").append(thatValue).append(" ").append(thatUnit);
        if (error) sb.append(", msg=").append(errorMessage);
        else if (resultString != null) sb.append(", res=").append(resultString);
        else sb.append(", res=").append(resultValue).append(" ").append(resultUnit);
        if (user != null) sb.append(", uid=").append(user.getId());
        return sb.toString();
    }
}