package com.app.quantitymeasurement.controller;

import lombok.extern.slf4j.Slf4j;
import com.app.quantitymeasurement.dto.request.*;
import com.app.quantitymeasurement.dto.response.*;
import com.app.quantitymeasurement.entity.User;
import com.app.quantitymeasurement.security.UserPrincipal;
import com.app.quantitymeasurement.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j @Validated @RestController @RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {
    private final AuthenticationService authService;
    public AuthController(AuthenticationService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        UserPrincipal p = (UserPrincipal) authentication.getPrincipal();
        User u = p.getUser();
        return ResponseEntity.ok(AuthResponse.builder().email(u.getEmail()).name(u.getName()).role(u.getRole().name()).build());
    }

    @PostMapping("/otp/send")
    @Operation(summary = "Send OTP to email")
    public ResponseEntity<MessageResponse> sendOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.sendOtp(request.getEmail()));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP code")
    public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.getEmail(), request.getOtp()));
    }

    @PutMapping("/forgotPassword/{email}")
    public ResponseEntity<MessageResponse> forgotPassword(@PathVariable String email, @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(email, request));
    }

    @PutMapping("/resetPassword/{email}")
    public ResponseEntity<MessageResponse> resetPassword(@PathVariable String email,
            @RequestParam @NotBlank String currentPassword,
            @RequestParam @NotBlank @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&*()+\\-=])(?=.*\\d).{8,}$") String newPassword) {
        return ResponseEntity.ok(authService.resetPassword(email, currentPassword, newPassword));
    }
}