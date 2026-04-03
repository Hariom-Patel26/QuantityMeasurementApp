package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.dto.response.QuantityDTO;
import com.app.quantitymeasurement.dto.request.QuantityMeasurementDTO;
import com.app.quantitymeasurement.entity.User;
import java.util.List;

public interface IQuantityMeasurementService {
    QuantityMeasurementDTO compare(QuantityDTO thisQ, QuantityDTO thatQ, User user);
    QuantityMeasurementDTO convert(QuantityDTO thisQ, QuantityDTO thatQ, User user);
    QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, User user);
    QuantityMeasurementDTO add(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ, User user);
    QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, User user);
    QuantityMeasurementDTO subtract(QuantityDTO thisQ, QuantityDTO thatQ, QuantityDTO targetQ, User user);
    QuantityMeasurementDTO divide(QuantityDTO thisQ, QuantityDTO thatQ, User user);
    List<QuantityMeasurementDTO> getHistoryByOperation(String operation, User user);
    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType, User user);
    long getOperationCount(String operation, User user);
    List<QuantityMeasurementDTO> getErrorHistory();
}