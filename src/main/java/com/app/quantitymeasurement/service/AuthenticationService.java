package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.dto.request.*;
import com.app.quantitymeasurement.dto.response.*;
import com.app.quantitymeasurement.entity.*;
import com.app.quantitymeasurement.enums.*;
import com.app.quantitymeasurement.repository.*;
import com.app.quantitymeasurement.security.UserPrincipal;
import com.app.quantitymeasurement.security.jwt.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.time.LocalDateTime;@Slf4j
@Service
public class AuthenticationService {

    /*
     * OTP expiry time in minutes.
     * After this duration, OTP becomes invalid.
     */
    private static final int OTP_EXPIRY_MINUTES = 5;

    /*
     * Maximum number of OTP requests allowed per email in 1 hour.
     * Prevents abuse/spamming of OTP service.
     */
    private static final int MAX_OTP_PER_HOUR = 5;

    // Spring Security authentication manager for login/register authentication
    private final AuthenticationManager authenticationManager;

    // Repository to manage User entity
    private final UserRepository userRepository;

    // Repository to manage OTP tokens
    private final OtpTokenRepository otpTokenRepository;

    // Password encoder (e.g., BCrypt) for hashing passwords & OTP
    private final PasswordEncoder passwordEncoder;

    // JWT provider for generating access tokens
    private final JwtTokenProvider jwtTokenProvider;

    // Service to send emails (OTP, login alerts, etc.)
    private final EmailService emailService;

    // Secure random generator for OTP creation
    private final SecureRandom secureRandom = new SecureRandom();

    /*
     * Constructor-based dependency injection
     */
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserRepository userRepository,
                                 OtpTokenRepository otpTokenRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtTokenProvider jwtTokenProvider,
                                 EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
    }

    /*
     * =========================
     * USER REGISTRATION METHOD
     * =========================
     */
    public AuthResponse register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");

        /*
         * Create new user object
         * - Password is encoded before saving
         * - Default provider = LOCAL
         * - Default role = USER
         */
        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider(AuthProvider.LOCAL)
                .role(Role.USER)
                .build();

        // Save user to database
        userRepository.save(newUser);

        /*
         * Authenticate immediately after registration
         * This avoids requiring separate login after signup
         */
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(auth);

        // Extract user details
        UserPrincipal p = (UserPrincipal) auth.getPrincipal();

        // Send welcome/registration email
        emailService.sendRegistrationEmail(
                newUser.getEmail(),
                newUser.getName() != null ? newUser.getName() : "there"
        );

        // Return response with token and user details
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(p.getEmail())
                .name(p.getUser().getName())
                .role(p.getUser().getRole().name())
                .build();
    }

    /*
     * =========================
     * LOGIN METHOD
     * =========================
     */
    public AuthResponse login(AuthRequest request) {
        try {
            // Authenticate user credentials
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Store authentication in context
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(auth);

            UserPrincipal p = (UserPrincipal) auth.getPrincipal();

            // Send login alert email
            emailService.sendLoginNotificationEmail(request.getEmail());

            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .email(p.getEmail())
                    .name(p.getUser().getName())
                    .role(p.getUser().getRole().name())
                    .build();

        } catch (AuthenticationException ex) {

            // If authentication fails
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password!"
            );
        }
    }

    /*
     * =========================
     * SEND OTP METHOD
     * =========================
     */
    @Transactional
    public MessageResponse sendOtp(String email) {

        /*
         * Security: Do NOT reveal whether email exists
         * Always return same message
         */
        if (!userRepository.existsByEmail(email))
            return new MessageResponse(
                    "If an account with that email exists, a verification code has been sent."
            );

        /*
         * Check OTP request limit (max 5 per hour)
         */
        long recent = otpTokenRepository.countByEmailAndCreatedAtAfter(
                email,
                LocalDateTime.now().minusHours(1)
        );

        if (recent >= MAX_OTP_PER_HOUR)
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many requests. Try again later."
            );

        /*
         * Generate 6-digit OTP
         */
        String raw = String.valueOf(
                secureRandom.nextInt(900000) + 100000
        );

        /*
         * Store OTP securely (hashed, not plain text)
         */
        OtpToken otp = OtpToken.builder()
                .email(email)
                .otpHash(passwordEncoder.encode(raw))
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpTokenRepository.save(otp);

        // Send OTP email
        emailService.sendOtpEmail(email, raw);

        return new MessageResponse("Verification code sent to your email.");
    }

    /*
     * =========================
     * VERIFY OTP METHOD
     * =========================
     */
    @Transactional
    public MessageResponse verifyOtp(String email, String otp) {

        // Get latest unverified OTP
        OtpToken token = otpTokenRepository
                .findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No pending code found. Request a new one."
                ));

        // Check expiry
        if (token.isExpired())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Code expired. Request a new one."
            );

        // Check if too many attempts
        if (token.isLockedOut())
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed attempts. Request a new code."
            );

        // Validate OTP
        if (!passwordEncoder.matches(otp, token.getOtpHash())) {

            // Increment failed attempts
            token.setAttempts(token.getAttempts() + 1);
            otpTokenRepository.save(token);

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid code. " + (5 - token.getAttempts()) + " attempts left."
            );
        }

        // Mark OTP as verified
        token.setVerified(true);
        otpTokenRepository.save(token);

        return new MessageResponse("Verification code verified successfully.");
    }

    /*
     * =========================
     * FORGOT PASSWORD METHOD
     * =========================
     */
    @Transactional
    public MessageResponse forgotPassword(String email, ForgotPasswordRequest request) {

        /*
         * Ensure OTP verification is done before password reset
         */
        OtpToken verified = otpTokenRepository
                .findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Email verification required."
                ));

        /*
         * Check if verification session expired
         */
        if (verified.getExpiresAt().plusMinutes(10)
                .isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Verification session expired."
            );

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + email
                ));

        // Update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Delete all OTPs for security
        otpTokenRepository.deleteAllByEmail(email);

        // Send confirmation email
        emailService.sendForgotPasswordEmail(email);

        return new MessageResponse("Password has been changed successfully!");
    }

    /*
     * =========================
     * RESET PASSWORD (LOGGED IN USER)
     * =========================
     */
    public MessageResponse resetPassword(String email,
                                         String currentPassword,
                                         String newPassword) {

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found: " + email
                ));

        // Validate current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword()))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current password is incorrect!"
            );

        // Set new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Send notification email
        emailService.sendPasswordResetEmail(email);

        return new MessageResponse("Password reset successfully!");
    }
}