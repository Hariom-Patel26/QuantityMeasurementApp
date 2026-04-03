package com.app.quantitymeasurement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid address")
    private String email;

    private String otp;
}