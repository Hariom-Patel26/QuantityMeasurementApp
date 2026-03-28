package com.app.quantitymeasurement.dto;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementDTO {

   
    private Double thisValue;

   
    private String thisUnit;

  
    private String thisMeasurementType;


    private Double thatValue;

  
    private String thatUnit;

   
    private String thatMeasurementType;

 
    private String operation;


    private String resultString;


    private Double resultValue;

    
    private String resultUnit;

  
    private String resultMeasurementType;


    private String errorMessage;

  
    private boolean error;

   
    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity entity) {
        if (entity == null) return null;
        return QuantityMeasurementDTO.builder()
            .thisValue(entity.getThisValue())
            .thisUnit(entity.getThisUnit())
            .thisMeasurementType(entity.getThisMeasurementType())
            .thatValue(entity.getThatValue())
            .thatUnit(entity.getThatUnit())
            .thatMeasurementType(entity.getThatMeasurementType())
            .operation(entity.getOperation())
            .resultString(entity.getResultString())
            .resultValue(entity.getResultValue() != null ? entity.getResultValue() : 0.0)
            .resultUnit(entity.getResultUnit())
            .resultMeasurementType(entity.getResultMeasurementType())
            .errorMessage(entity.getErrorMessage())
            .error(entity.isError())
            .build();
    }


    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setThisValue(this.thisValue);
        entity.setThisUnit(this.thisUnit);
        entity.setThisMeasurementType(this.thisMeasurementType);
        entity.setThatValue(this.thatValue);
        entity.setThatUnit(this.thatUnit);
        entity.setThatMeasurementType(this.thatMeasurementType);
        entity.setOperation(this.operation);
        entity.setResultString(this.resultString);
        entity.setResultValue(this.resultValue);
        entity.setResultUnit(this.resultUnit);
        entity.setResultMeasurementType(this.resultMeasurementType);
        entity.setErrorMessage(this.errorMessage);
        entity.setError(this.error);
        return entity;
    }


    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream()
            .map(QuantityMeasurementDTO::fromEntity)
            .collect(Collectors.toList());
    }


    public static List<QuantityMeasurementEntity> toEntityList(List<QuantityMeasurementDTO> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream()
            .map(QuantityMeasurementDTO::toEntity)
            .collect(Collectors.toList());
    }
}
