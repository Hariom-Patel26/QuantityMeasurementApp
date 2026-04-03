package com.app.quantitymeasurement.service;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import org.springframework.stereotype.Service;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.dto.response.QuantityDTO;
import com.app.quantitymeasurement.dto.request.QuantityMeasurementDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.entity.User;
import com.app.quantitymeasurement.model.QuantityModel;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.IMeasurable;

@Slf4j
@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final double EPSILON = 1e-6;
    private final QuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(QuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    private enum Operation { COMPARE, CONVERT, ADD, SUBTRACT, DIVIDE }
    private enum ArithOp { ADD, SUBTRACT, DIVIDE }

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO thisQ, QuantityDTO thatQ, User user) {
        QuantityModel<IMeasurable> q1 = toModel(thisQ), q2 = toModel(thatQ);
        try {
            if (!q1.getUnit().getMeasurementType().equals(q2.getUnit().getMeasurementType()))
                throw new QuantityMeasurementException("compare Error: Cannot compare different categories: " + q1.getUnit().getMeasurementType() + " and " + q2.getUnit().getMeasurementType());
            boolean result = cmpBase(q1, q2);
            QuantityMeasurementEntity e = mkEntity(q1, q2, "compare", String.valueOf(result), null, null, null, false, null);
            e.setUser(user);
            repository.save(e);
            return QuantityMeasurementDTO.fromEntity(e);
        } catch (QuantityMeasurementException ex) { saveErr(q1, q2, "compare", ex.getMessage(), user); throw ex;
        } catch (Exception ex) { saveErr(q1, q2, "compare", ex.getMessage(), user); throw new QuantityMeasurementException("compare Error: " + ex.getMessage(), ex); }
    }

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO thisQ, QuantityDTO tgtQ, User user) {
        QuantityModel<IMeasurable> src = toModel(thisQ), tgt = toModel(tgtQ);
        try {
            double r = (src.getUnit() instanceof com.app.quantitymeasurement.unit.TemperatureUnit)
                ? tgt.getUnit().convertFromBaseUnit(src.getUnit().convertToBaseUnit(src.getValue()))
                : tgt.getUnit().convertFromBaseUnit(src.getUnit().convertToBaseUnit(src.getValue()));
            QuantityMeasurementEntity e = mkEntity(src, tgt, "convert", null, r, tgt.getUnit().getUnitName(), tgt.getUnit().getMeasurementType(), false, null);
            e.setUser(user);
            repository.save(e);
            return QuantityMeasurementDTO.fromEntity(e);
        } catch (Exception ex) { saveErr(src, tgt, "convert", ex.getMessage(), user); throw new QuantityMeasurementException("convert Error: " + ex.getMessage(), ex); }
    }

    @Override public QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, User user) { return add(thisQ, thatQ, thisQ, user); }

    @Override
    public QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO tgtQ, User user) {
        QuantityModel<IMeasurable> q1 = toModel(thisQ), q2 = toModel(thatQ), tgt = toModel(tgtQ);
        try {
            validateArith(q1, q2, tgt.getUnit(), true);
            double r = tgt.getUnit().convertFromBaseUnit(doArith(q1, q2, ArithOp.ADD));
            QuantityMeasurementEntity e = mkEntity(q1, q2, "add", null, r, tgt.getUnit().getUnitName(), tgt.getUnit().getMeasurementType(), false, null);
            e.setUser(user); repository.save(e);
            return QuantityMeasurementDTO.fromEntity(e);
        } catch (QuantityMeasurementException ex) { saveErr(q1, q2, "add", ex.getMessage(), user); throw ex;
        } catch (Exception ex) { saveErr(q1, q2, "add", ex.getMessage(), user); throw new QuantityMeasurementException("add Error: " + ex.getMessage(), ex); }
    }

    @Override public QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, User user) { return subtract(thisQ, thatQ, thisQ, user); }

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO tgtQ, User user) {
        QuantityModel<IMeasurable> q1 = toModel(thisQ), q2 = toModel(thatQ), tgt = toModel(tgtQ);
        try {
            validateArith(q1, q2, tgt.getUnit(), true);
            double r = tgt.getUnit().convertFromBaseUnit(doArith(q1, q2, ArithOp.SUBTRACT));
            QuantityMeasurementEntity e = mkEntity(q1, q2, "subtract", null, r, tgt.getUnit().getUnitName(), tgt.getUnit().getMeasurementType(), false, null);
            e.setUser(user); repository.save(e);
            return QuantityMeasurementDTO.fromEntity(e);
        } catch (QuantityMeasurementException ex) { saveErr(q1, q2, "subtract", ex.getMessage(), user); throw ex;
        } catch (Exception ex) { saveErr(q1, q2, "subtract", ex.getMessage(), user); throw new QuantityMeasurementException("subtract Error: " + ex.getMessage(), ex); }
    }

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO thisQ, QuantityDTO thatQ, User user) {
        QuantityModel<IMeasurable> q1 = toModel(thisQ), q2 = toModel(thatQ);
        try {
            validateArith(q1, q2, null, false);
            double r = doArith(q1, q2, ArithOp.DIVIDE);
            QuantityMeasurementEntity e = mkEntity(q1, q2, "divide", null, r, null, null, false, null);
            e.setUser(user); repository.save(e);
            return QuantityMeasurementDTO.fromEntity(e);
        } catch (ArithmeticException ex) { saveErr(q1, q2, "divide", ex.getMessage(), user); throw ex;
        } catch (QuantityMeasurementException ex) { saveErr(q1, q2, "divide", ex.getMessage(), user); throw ex;
        } catch (Exception ex) { saveErr(q1, q2, "divide", ex.getMessage(), user); throw new QuantityMeasurementException("divide Error: " + ex.getMessage(), ex); }
    }

    @Override public List<QuantityMeasurementDTO> getHistoryByOperation(String op, User user) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByUserAndOperation(user, op));
    }
    @Override public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String type, User user) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByUserAndThisMeasurementType(user, type));
    }
    @Override public long getOperationCount(String op, User user) {
        return repository.countByUserAndOperationAndErrorFalse(user, op);
    }
    @Override public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByErrorTrue());
    }

    private QuantityModel<IMeasurable> toModel(QuantityDTO q) {
        if (q == null) throw new IllegalArgumentException("QuantityDTO cannot be null");
        return new QuantityModel<>(q.getValue(), getUnit(q.getMeasurementType(), q.getUnit()));
    }
    private IMeasurable getUnit(String type, String unit) {
        switch (type) {
            case "LengthUnit": return com.app.quantitymeasurement.unit.LengthUnit.valueOf(unit);
            case "WeightUnit": return com.app.quantitymeasurement.unit.WeightUnit.valueOf(unit);
            case "VolumeUnit": return com.app.quantitymeasurement.unit.VolumeUnit.valueOf(unit);
            case "TemperatureUnit": return com.app.quantitymeasurement.unit.TemperatureUnit.valueOf(unit);
            default: throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }
    private <U extends IMeasurable> boolean cmpBase(QuantityModel<U> q1, QuantityModel<U> q2) {
        return Math.abs(q1.getUnit().convertToBaseUnit(q1.getValue()) - q2.getUnit().convertToBaseUnit(q2.getValue())) < EPSILON;
    }
    private <U extends IMeasurable> void validateArith(QuantityModel<U> q1, QuantityModel<U> q2, U tgt, boolean tgtReq) {
        if (q1 == null || q2 == null) throw new IllegalArgumentException("Operands cannot be null");
        String t1 = q1.getUnit().getMeasurementType(), t2 = q2.getUnit().getMeasurementType();
        if (!t1.equals(t2)) throw new IllegalArgumentException("Cannot do arithmetic between " + t1 + " and " + t2);
        if (t1.equals("TemperatureUnit")) throw new UnsupportedOperationException("Arithmetic not supported for temperature");
        if (tgtReq && tgt == null) throw new IllegalArgumentException("Target unit required");
    }
    private <U extends IMeasurable> double doArith(QuantityModel<U> q1, QuantityModel<U> q2, ArithOp op) {
        double b1 = q1.getUnit().convertToBaseUnit(q1.getValue()), b2 = q2.getUnit().convertToBaseUnit(q2.getValue());
        if (op == ArithOp.DIVIDE && b2 == 0) throw new ArithmeticException("Division by zero is not allowed");
        switch (op) { case ADD: return b1 + b2; case SUBTRACT: return b1 - b2; case DIVIDE: return b1 / b2; default: throw new IllegalArgumentException("Invalid op"); }
    }
    private QuantityMeasurementEntity mkEntity(QuantityModel<IMeasurable> q1, QuantityModel<IMeasurable> q2, String op, String rs, Double rv, String ru, String rt, boolean err, String em) {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setThisValue(q1.getValue()); e.setThisUnit(q1.getUnit().getUnitName()); e.setThisMeasurementType(q1.getUnit().getMeasurementType());
        e.setThatValue(q2.getValue()); e.setThatUnit(q2.getUnit().getUnitName()); e.setThatMeasurementType(q2.getUnit().getMeasurementType());
        e.setOperation(op); e.setResultString(rs); e.setResultValue(rv); e.setResultUnit(ru); e.setResultMeasurementType(rt); e.setError(err); e.setErrorMessage(em);
        return e;
    }
    private void saveErr(QuantityModel<IMeasurable> q1, QuantityModel<IMeasurable> q2, String op, String msg, User user) {
        try { QuantityMeasurementEntity e = mkEntity(q1, q2, op, null, null, null, null, true, msg); e.setUser(user); repository.save(e); }
        catch (Exception ex) { log.error("Failed to save error entity: {}", ex.getMessage()); }
    }
}