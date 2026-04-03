package com.app.quantitymeasurement.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.app.quantitymeasurement.dto.request.*;
import com.app.quantitymeasurement.dto.response.AuthResponse;
import com.app.quantitymeasurement.entity.OtpToken;
import com.app.quantitymeasurement.entity.User;
import com.app.quantitymeasurement.enums.AuthProvider;
import com.app.quantitymeasurement.enums.Role;
import com.app.quantitymeasurement.repository.OtpTokenRepository;
import com.app.quantitymeasurement.repository.UserRepository;
import com.app.quantitymeasurement.security.UserPrincipal;
import com.app.quantitymeasurement.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc         mockMvc;
    @Autowired private ObjectMapper    objectMapper;
    @Autowired private UserRepository  userRepository;
    @Autowired private OtpTokenRepository otpTokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @MockBean private com.app.quantitymeasurement.service.EmailService emailService;

    private static final String BASE = "/api/v1/auth";
    private static final String VALID_PASSWORD = "Strong@123";

    @BeforeEach
    void setUp() {
        otpTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    // =========================================================================
    // POST /register
    // =========================================================================

    @Test void testRegister_ValidRequest_Returns201() throws Exception {
        RegisterRequest req = new RegisterRequest("alice@example.com", VALID_PASSWORD, "Alice");
        mockMvc.perform(post(BASE + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test void testRegister_DuplicateEmail_Returns409() throws Exception {
        saveLocalUser("dup@example.com", VALID_PASSWORD);
        RegisterRequest req = new RegisterRequest("dup@example.com", VALID_PASSWORD, "Dup");
        mockMvc.perform(post(BASE + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isConflict());
    }

    @Test void testRegister_WeakPassword_Returns400() throws Exception {
        RegisterRequest req = new RegisterRequest("x@example.com", "short", "X");
        mockMvc.perform(post(BASE + "/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // POST /login
    // =========================================================================

    @Test void testLogin_ValidCredentials_Returns200() throws Exception {
        saveLocalUser("bob@example.com", VALID_PASSWORD);
        AuthRequest req = new AuthRequest("bob@example.com", VALID_PASSWORD);
        mockMvc.perform(post(BASE + "/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test void testLogin_WrongPassword_Returns401() throws Exception {
        saveLocalUser("carol@example.com", VALID_PASSWORD);
        AuthRequest req = new AuthRequest("carol@example.com", "WrongPwd@9");
        mockMvc.perform(post(BASE + "/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // GET /me
    // =========================================================================

    @Test void testGetMe_WithValidJwt_Returns200() throws Exception {
        User user = saveLocalUser("dan@example.com", VALID_PASSWORD);
        String token = generateToken(user);
        mockMvc.perform(get(BASE + "/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("dan@example.com"));
    }

    @Test void testGetMe_WithoutJwt_Returns401() throws Exception {
        mockMvc.perform(get(BASE + "/me")).andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // POST /otp/send + POST /otp/verify
    // =========================================================================

    @Test void testSendOtp_ExistingUser_Returns200() throws Exception {
        saveLocalUser("otp@example.com", VALID_PASSWORD);
        OtpRequest req = new OtpRequest("otp@example.com", null);
        mockMvc.perform(post(BASE + "/otp/send").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    // =========================================================================
    // PUT /forgotPassword/{email} (requires verified OTP)
    // =========================================================================

    @Test void testForgotPassword_WithVerifiedOtp_Returns200() throws Exception {
        saveLocalUser("grace@example.com", VALID_PASSWORD);
        createVerifiedOtp("grace@example.com");

        ForgotPasswordRequest req = new ForgotPasswordRequest("NewStrong@456");
        mockMvc.perform(put(BASE + "/forgotPassword/grace@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Password has been changed successfully!"));
    }

    @Test void testForgotPassword_WithoutVerifiedOtp_Returns403() throws Exception {
        saveLocalUser("notp@example.com", VALID_PASSWORD);

        ForgotPasswordRequest req = new ForgotPasswordRequest("NewStrong@456");
        mockMvc.perform(put(BASE + "/forgotPassword/notp@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test void testForgotPassword_WeakPassword_Returns400() throws Exception {
        ForgotPasswordRequest req = new ForgotPasswordRequest("weakpassword");
        mockMvc.perform(put(BASE + "/forgotPassword/any@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    @Test void testForgotPassword_NewPasswordCanBeUsedToLogin() throws Exception {
        saveLocalUser("ivan@example.com", VALID_PASSWORD);
        createVerifiedOtp("ivan@example.com");

        String newPassword = "Updated@789";
        ForgotPasswordRequest req = new ForgotPasswordRequest(newPassword);
        mockMvc.perform(put(BASE + "/forgotPassword/ivan@example.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());

        // Old password rejected
        mockMvc.perform(post(BASE + "/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthRequest("ivan@example.com", VALID_PASSWORD))))
            .andExpect(status().isUnauthorized());

        // New password accepted
        mockMvc.perform(post(BASE + "/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthRequest("ivan@example.com", newPassword))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    // =========================================================================
    // PUT /resetPassword/{email}
    // =========================================================================

    @Test void testResetPassword_CorrectCurrentPassword_Returns200() throws Exception {
        User user = saveLocalUser("judy@example.com", VALID_PASSWORD);
        String token = generateToken(user);
        mockMvc.perform(put(BASE + "/resetPassword/judy@example.com")
                .header("Authorization", "Bearer " + token)
                .param("currentPassword", VALID_PASSWORD)
                .param("newPassword", "NewStrong@456"))
            .andExpect(status().isOk());
    }

    @Test void testResetPassword_WithoutJwt_Returns401() throws Exception {
        saveLocalUser("mary@example.com", VALID_PASSWORD);
        mockMvc.perform(put(BASE + "/resetPassword/mary@example.com")
                .param("currentPassword", VALID_PASSWORD)
                .param("newPassword", "NewStrong@456"))
            .andExpect(status().isUnauthorized());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private User saveLocalUser(String email, String rawPassword) {
        User user = User.builder().email(email)
            .password(passwordEncoder.encode(rawPassword))
            .provider(AuthProvider.LOCAL).role(Role.USER).build();
        return userRepository.save(user);
    }

    private String generateToken(User user) {
        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        return jwtTokenProvider.generateToken(auth);
    }

    /** Creates a verified OTP record in the database for the given email. */
    private void createVerifiedOtp(String email) {
        OtpToken otp = OtpToken.builder()
            .email(email)
            .otpHash(passwordEncoder.encode("123456"))
            .expiresAt(LocalDateTime.now().plusMinutes(10))
            .verified(true)
            .build();
        otpTokenRepository.save(otp);
    }
}