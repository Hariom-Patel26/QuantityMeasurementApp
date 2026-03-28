package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", 
	 description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private static final Logger logger = Logger.getLogger(
        QuantityMeasurementController.class.getName()
    );

    @Autowired
    private IQuantityMeasurementService quantityMeasurementService;

    
    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities",
               description = "Compares two quantities for equality after converting to base units")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
    	
        logger.info("POST /compare");
        
        return ResponseEntity.ok(
        		quantityMeasurementService.compare(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO()
        ));
    }


    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a different unit",
               description = "Converts a quantity from its current unit to the specified target unit")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /convert");
        return ResponseEntity.ok(quantityMeasurementService.convert(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO()
        ));
    }


    @PostMapping("/add")
    @Operation(summary = "Add two quantities",
               description = "Adds two quantities, with an optional target unit for the result")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /add");
        QuantityMeasurementDTO result = (quantityInputDTO.getTargetUnitDTO() != null)
            ? quantityMeasurementService.add(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO(),
                quantityInputDTO.getTargetUnitDTO())
            : quantityMeasurementService.add(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }


    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities",
               description = "Subtracts the second quantity from the first, with an optional target unit")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /subtract");
        QuantityMeasurementDTO result = (quantityInputDTO.getTargetUnitDTO() != null)
            ? quantityMeasurementService.subtract(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO(),
                quantityInputDTO.getTargetUnitDTO())
            : quantityMeasurementService.subtract(
                quantityInputDTO.getThisQuantityDTO(),
                quantityInputDTO.getThatQuantityDTO());
        return ResponseEntity.ok(result);
    }

    
    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities",
               description = "Divides the first quantity by the second and returns the numeric ratio")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(
            @Valid @RequestBody QuantityInputDTO quantityInputDTO) {
        logger.info("POST /divide");
        return ResponseEntity.ok(quantityMeasurementService.divide(
            quantityInputDTO.getThisQuantityDTO(),
            quantityInputDTO.getThatQuantityDTO()
        ));
    }


    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get operation history by type",
               description = "Returns all measurement records for the specified operation type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOperationHistory(
            @Parameter(description = "Operation type — compare, convert, add, subtract, divide")
            @PathVariable String operation) {
        logger.info("GET /history/operation/" + operation);
        return ResponseEntity.ok(quantityMeasurementService.getHistoryByOperation(operation));
    }


    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get measurement history by type",
               description = "Returns all measurement records for the specified measurement type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getMeasurementHistory(
            @Parameter(description = "Measurement type — LengthUnit, WeightUnit, VolumeUnit, TemperatureUnit")
            @PathVariable String measurementType) {
        logger.info("GET /history/type/" + measurementType);
        return ResponseEntity.ok(quantityMeasurementService.getHistoryByMeasurementType(measurementType));
    }


    @GetMapping("/history/errored")
    @Operation(summary = "Get error history",
               description = "Returns all measurement records that resulted in an error")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        logger.info("GET /history/errored");
        return ResponseEntity.ok(quantityMeasurementService.getErrorHistory());
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Get operation count",
               description = "Returns the count of successful operations for the specified operation type")
    public ResponseEntity<Long> getOperationCount(
            @Parameter(description = "Operation type to count — COMPARE, CONVERT, ADD, SUBTRACT, DIVIDE")
            @PathVariable String operation) {
        logger.info("GET /count/" + operation);
        return ResponseEntity.ok(quantityMeasurementService.getOperationCount(operation));
    }
}
