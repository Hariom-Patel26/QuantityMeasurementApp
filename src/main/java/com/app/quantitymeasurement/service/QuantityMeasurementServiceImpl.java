package com.app.quantitymeasurement.service;

import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.IMeasurable;


@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = Logger.getLogger(
        QuantityMeasurementServiceImpl.class.getName()
    );

    @Autowired
    private QuantityMeasurementRepository repository;

 
    private enum Operation {
        COMPARE, CONVERT, ADD, SUBTRACT, DIVIDE
    }

    
    private enum ArithmeticOperation {
        ADD, SUBTRACT, DIVIDE
    }

    
    @Override
    public QuantityMeasurementDTO compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> q1 = convertDtoToModel(thisQuantityDTO);
        QuantityModel<IMeasurable> q2 = convertDtoToModel(thatQuantityDTO);

        try {
            if (!q1.getUnit().getMeasurementType().equals(q2.getUnit().getMeasurementType())) {
                throw new QuantityMeasurementException(
                    "compare Error: Cannot compare different measurement categories: "
                    + q1.getUnit().getMeasurementType() + " and " + q2.getUnit().getMeasurementType());
            }

            boolean result = compareBaseValues(q1, q2);
            QuantityMeasurementEntity entity = buildEntity(q1, q2,
                Operation.COMPARE.name().toLowerCase(),
                String.valueOf(result), null, null, null, false, null);
            repository.save(entity);

            logger.fine("COMPARE: " + q1 + " vs " + q2 + " => " + result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveErrorEntity(q1, q2, Operation.COMPARE.name().toLowerCase(), e.getMessage());
            throw e;
        } catch (Exception e) {
            saveErrorEntity(q1, q2, Operation.COMPARE.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("compare Error: " + e.getMessage(), e);
        }
    }

 
    @Override
    public QuantityMeasurementDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> source = convertDtoToModel(thisQuantityDTO);
        QuantityModel<IMeasurable> target = convertDtoToModel(targetUnitDTO);

        try {
            double result = (source.getUnit() instanceof com.app.quantitymeasurement.unit.TemperatureUnit)
                ? convertTemperatureUnit(source, target.getUnit())
                : target.getUnit().convertFromBaseUnit(source.getUnit().convertToBaseUnit(source.getValue()));

            QuantityMeasurementEntity entity = buildEntity(source, target,
                Operation.CONVERT.name().toLowerCase(), null, result,
                target.getUnit().getUnitName(), target.getUnit().getMeasurementType(), false, null);
            repository.save(entity);

            logger.fine("CONVERT: " + source + " => " + result + " " + target.getUnit().getUnitName());
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (Exception e) {
            saveErrorEntity(source, target, Operation.CONVERT.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("convert Error: " + e.getMessage(), e);
        }
    }


    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return add(thisQuantityDTO, thatQuantityDTO, thisQuantityDTO);
    }

   
    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQuantityDTO,
                                      QuantityDTO thatQuantityDTO,
                                      QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> q1     = convertDtoToModel(thisQuantityDTO);
        QuantityModel<IMeasurable> q2     = convertDtoToModel(thatQuantityDTO);
        QuantityModel<IMeasurable> target = convertDtoToModel(targetUnitDTO);

        try {
            validateArithmeticOperands(q1, q2, target.getUnit(), true);
            double result = target.getUnit().convertFromBaseUnit(
                performArithmetic(q1, q2, ArithmeticOperation.ADD));

            QuantityMeasurementEntity entity = buildEntity(q1, q2,
                Operation.ADD.name().toLowerCase(), null, result,
                target.getUnit().getUnitName(), target.getUnit().getMeasurementType(), false, null);
            repository.save(entity);

            logger.fine("ADD: " + q1 + " + " + q2 + " => " + result + " " + target.getUnit().getUnitName());
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveErrorEntity(q1, q2, Operation.ADD.name().toLowerCase(), e.getMessage());
            throw e;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            saveErrorEntity(q1, q2, Operation.ADD.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        } catch (Exception e) {
            saveErrorEntity(q1, q2, Operation.ADD.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("add Error: " + e.getMessage(), e);
        }
    }


    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return subtract(thisQuantityDTO, thatQuantityDTO, thisQuantityDTO);
    }


    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQuantityDTO,
                                           QuantityDTO thatQuantityDTO,
                                           QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> q1     = convertDtoToModel(thisQuantityDTO);
        QuantityModel<IMeasurable> q2     = convertDtoToModel(thatQuantityDTO);
        QuantityModel<IMeasurable> target = convertDtoToModel(targetUnitDTO);

        try {
            validateArithmeticOperands(q1, q2, target.getUnit(), true);
            double result = target.getUnit().convertFromBaseUnit(
                performArithmetic(q1, q2, ArithmeticOperation.SUBTRACT));

            QuantityMeasurementEntity entity = buildEntity(q1, q2,
                Operation.SUBTRACT.name().toLowerCase(), null, result,
                target.getUnit().getUnitName(), target.getUnit().getMeasurementType(), false, null);
            repository.save(entity);

            logger.fine("SUBTRACT: " + q1 + " - " + q2 + " => " + result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveErrorEntity(q1, q2, Operation.SUBTRACT.name().toLowerCase(), e.getMessage());
            throw e;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            saveErrorEntity(q1, q2, Operation.SUBTRACT.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage(), e);
        } catch (Exception e) {
            saveErrorEntity(q1, q2, Operation.SUBTRACT.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("subtract Error: " + e.getMessage(), e);
        }
    }

   
    @Override
    public QuantityMeasurementDTO divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> q1 = convertDtoToModel(thisQuantityDTO);
        QuantityModel<IMeasurable> q2 = convertDtoToModel(thatQuantityDTO);

        try {
            validateArithmeticOperands(q1, q2, null, false);
            double result = performArithmetic(q1, q2, ArithmeticOperation.DIVIDE);

            QuantityMeasurementEntity entity = buildEntity(q1, q2,
                Operation.DIVIDE.name().toLowerCase(), null, result, null, null, false, null);
            repository.save(entity);

            logger.fine("DIVIDE: " + q1 + " / " + q2 + " => " + result);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (ArithmeticException e) {
            saveErrorEntity(q1, q2, Operation.DIVIDE.name().toLowerCase(), e.getMessage());
            throw e;
        } catch (QuantityMeasurementException e) {
            saveErrorEntity(q1, q2, Operation.DIVIDE.name().toLowerCase(), e.getMessage());
            throw e;
        } catch (Exception e) {
            saveErrorEntity(q1, q2, Operation.DIVIDE.name().toLowerCase(), e.getMessage());
            throw new QuantityMeasurementException("divide Error: " + e.getMessage(), e);
        }
    }

 
    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByOperation(operation));
    }


    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(
            repository.findByThisMeasurementType(measurementType));
    }

  
    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndErrorFalse(operation);
    }


    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByErrorTrue());
    }


    private QuantityModel<IMeasurable> convertDtoToModel(QuantityDTO quantity) {
        if (quantity == null)
            throw new IllegalArgumentException("QuantityDTO cannot be null");
        return new QuantityModel<>(quantity.getValue(),
            getModelUnit(quantity.getMeasurementType(), quantity.getUnit()));
    }


    private IMeasurable getModelUnit(String measurementType, String unit) {
        switch (measurementType) {
            case "LengthUnit":      return com.app.quantitymeasurement.unit.LengthUnit.valueOf(unit);
            case "WeightUnit":      return com.app.quantitymeasurement.unit.WeightUnit.valueOf(unit);
            case "VolumeUnit":      return com.app.quantitymeasurement.unit.VolumeUnit.valueOf(unit);
            case "TemperatureUnit": return com.app.quantitymeasurement.unit.TemperatureUnit.valueOf(unit);
            default: throw new IllegalArgumentException("Unsupported measurement type: " + measurementType);
        }
    }


    private <U extends IMeasurable> boolean compareBaseValues(
            QuantityModel<U> q1, QuantityModel<U> q2) {
        return Double.compare(
            q1.getUnit().convertToBaseUnit(q1.getValue()),
            q2.getUnit().convertToBaseUnit(q2.getValue())) == 0;
    }

 
    private <U extends IMeasurable> double convertTemperatureUnit(
            QuantityModel<U> source, U targetUnit) {
        return targetUnit.convertFromBaseUnit(source.getUnit().convertToBaseUnit(source.getValue()));
    }


    private <U extends IMeasurable> void validateArithmeticOperands(
            QuantityModel<U> q1, QuantityModel<U> q2, U targetUnit, boolean targetRequired) {

        if (q1 == null || q2 == null)
            throw new IllegalArgumentException("Operands cannot be null");

        String type1 = q1.getUnit().getMeasurementType();
        String type2 = q2.getUnit().getMeasurementType();

        if (!type1.equals(type2)) {
            throw new IllegalArgumentException(
                "Cannot perform arithmetic between different measurement categories: "
                + type1 + " and " + type2);
        }
        if (type1.equals("TemperatureUnit")) {
            throw new UnsupportedOperationException(
                "Arithmetic operations are not supported for temperature units");
        }
        if (targetRequired && targetUnit == null)
            throw new IllegalArgumentException("Target unit is required");
    }


    private <U extends IMeasurable> double performArithmetic(
            QuantityModel<U> q1, QuantityModel<U> q2, ArithmeticOperation operation) {

        double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
        double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

        if (operation == ArithmeticOperation.DIVIDE && base2 == 0)
            throw new ArithmeticException("Division by zero is not allowed");

        DoubleBinaryOperator op;
        switch (operation) {
            case ADD:      op = (a, b) -> a + b; break;
            case SUBTRACT: op = (a, b) -> a - b; break;
            case DIVIDE:   op = (a, b) -> a / b; break;
            default: throw new IllegalArgumentException("Invalid arithmetic operation");
        }
        return op.applyAsDouble(base1, base2);
    }


    private QuantityMeasurementEntity buildEntity(
            QuantityModel<IMeasurable> q1,
            QuantityModel<IMeasurable> q2,
            String operation,
            String resultString,
            Double resultValue,
            String resultUnit,
            String resultType,
            boolean isError,
            String errorMessage) {

        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setThisValue(q1.getValue());
        entity.setThisUnit(q1.getUnit().getUnitName());
        entity.setThisMeasurementType(q1.getUnit().getMeasurementType());
        entity.setThatValue(q2.getValue());
        entity.setThatUnit(q2.getUnit().getUnitName());
        entity.setThatMeasurementType(q2.getUnit().getMeasurementType());
        entity.setOperation(operation);
        entity.setResultString(resultString);
        entity.setResultValue(resultValue);
        entity.setResultUnit(resultUnit);
        entity.setResultMeasurementType(resultType);
        entity.setError(isError);
        entity.setErrorMessage(errorMessage);
        return entity;
    }


    private void saveErrorEntity(QuantityModel<IMeasurable> q1,
                                 QuantityModel<IMeasurable> q2,
                                 String operation,
                                 String errorMessage) {
        try {
            repository.save(buildEntity(q1, q2, operation,
                null, null, null, null, true, errorMessage));
        } catch (Exception ex) {
            logger.severe("Failed to save error entity: " + ex.getMessage());
        }
    }
}
