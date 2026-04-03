package com.app.quantitymeasurement.controller;

import lombok.extern.slf4j.Slf4j;
import com.app.quantitymeasurement.dto.request.*;
import com.app.quantitymeasurement.entity.User;
import com.app.quantitymeasurement.security.UserPrincipal;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j @RestController @RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements")
public class QuantityMeasurementController {
    private final IQuantityMeasurementService svc;
    public QuantityMeasurementController(IQuantityMeasurementService svc) { this.svc = svc; }

    private User extractUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        Object p = auth.getPrincipal();
        return (p instanceof UserPrincipal) ? ((UserPrincipal) p).getUser() : null;
    }

    @PostMapping("/compare")
    public ResponseEntity<QuantityMeasurementDTO> compare(@Valid @RequestBody QuantityInputDTO dto, Authentication auth) {
        return ResponseEntity.ok(svc.compare(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), extractUser(auth)));
    }
    @PostMapping("/convert")
    public ResponseEntity<QuantityMeasurementDTO> convert(@Valid @RequestBody QuantityInputDTO dto, Authentication auth) {
        return ResponseEntity.ok(svc.convert(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), extractUser(auth)));
    }
    @PostMapping("/add")
    public ResponseEntity<QuantityMeasurementDTO> add(@Valid @RequestBody QuantityInputDTO dto, Authentication auth) {
        User u = extractUser(auth);
        return ResponseEntity.ok(dto.getTargetUnitDTO() != null
            ? svc.add(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), dto.getTargetUnitDTO(), u)
            : svc.add(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), u));
    }
    @PostMapping("/subtract")
    public ResponseEntity<QuantityMeasurementDTO> subtract(@Valid @RequestBody QuantityInputDTO dto, Authentication auth) {
        User u = extractUser(auth);
        return ResponseEntity.ok(dto.getTargetUnitDTO() != null
            ? svc.subtract(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), dto.getTargetUnitDTO(), u)
            : svc.subtract(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), u));
    }
    @PostMapping("/divide")
    public ResponseEntity<QuantityMeasurementDTO> divide(@Valid @RequestBody QuantityInputDTO dto, Authentication auth) {
        return ResponseEntity.ok(svc.divide(dto.getThisQuantityDTO(), dto.getThatQuantityDTO(), extractUser(auth)));
    }
    @GetMapping("/history/operation/{operation}")
    public ResponseEntity<List<QuantityMeasurementDTO>> getOpHistory(@PathVariable String operation, Authentication auth) {
        return ResponseEntity.ok(svc.getHistoryByOperation(operation, extractUser(auth)));
    }
    @GetMapping("/history/type/{measurementType}")
    public ResponseEntity<List<QuantityMeasurementDTO>> getTypeHistory(@PathVariable String measurementType, Authentication auth) {
        return ResponseEntity.ok(svc.getHistoryByMeasurementType(measurementType, extractUser(auth)));
    }
    @GetMapping("/history/errored")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        return ResponseEntity.ok(svc.getErrorHistory());
    }
    @GetMapping("/count/{operation}")
    public ResponseEntity<Long> getCount(@PathVariable String operation, Authentication auth) {
        return ResponseEntity.ok(svc.getOperationCount(operation, extractUser(auth)));
    }
}