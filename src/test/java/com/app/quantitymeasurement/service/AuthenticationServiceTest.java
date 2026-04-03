package com.app.quantitymeasurement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.app.quantitymeasurement.dto.request.AuthRequest;
import com.app.quantitymeasurement.dto.request.ForgotPasswordRequest;
import com.app.quantitymeasurement.dto.request.RegisterRequest;
import com.app.quantitymeasurement.dto.response.AuthResponse;
import com.app.quantitymeasurement.dto.response.MessageResponse;
import com.app.quantitymeasurement.entity.OtpToken;
import com.app.quantitymeasurement.entity.User;
import com.app.quantitymeasurement.enums.AuthProvider;
import com.app.quantitymeasurement.enums.Role;
import com.app.quantitymeasurement.repository.OtpTokenRepository;
import com.app.quantitymeasurement.repository.UserRepository;
import com.app.quantitymeasurement.security.UserPrincipal;
import com.app.quantitymeasurement.security.jwt.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private UserRepository userRepository;
	@Mock
	private OtpTokenRepository otpTokenRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private EmailService emailService;
	@Mock
	private Authentication authentication;

	@InjectMocks
	private AuthenticationService authService;

	private User localUser;
	private UserPrincipal userPrincipal;

	@BeforeEach
	void setUp() {
		localUser = User.builder().email("alice@example.com").name("Alice").password("$2a$10$hashedPassword")
				.provider(AuthProvider.LOCAL).role(Role.USER).build();
		userPrincipal = UserPrincipal.create(localUser);
	}

	// =========================================================================
	// register
	// =========================================================================

	@Test
	void testRegister_NewEmail_Returns201WithToken() {
		RegisterRequest req = new RegisterRequest("alice@example.com", "Strong@123", "Alice");
		when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
		when(passwordEncoder.encode("Strong@123")).thenReturn("$2a$10$hashedPassword");
		when(userRepository.save(any(User.class))).thenReturn(localUser);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userPrincipal);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt.token.here");

		AuthResponse response = authService.register(req);
		assertNotNull(response);
		assertEquals("jwt.token.here", response.getAccessToken());
		assertEquals("alice@example.com", response.getEmail());
	}

	@Test
	void testRegister_DuplicateEmail_ThrowsConflict() {
		RegisterRequest req = new RegisterRequest("dup@example.com", "Strong@123", "Dup");
		when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);
		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(req));
		assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
		verify(userRepository, never()).save(any());
	}

	@Test
	void testRegister_PasswordIsHashedBeforeSaving() {
		RegisterRequest req = new RegisterRequest("bob@example.com", "Strong@123", "Bob");
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode("Strong@123")).thenReturn("$2a$10$bcryptHash");
		when(userRepository.save(any(User.class))).thenReturn(localUser);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userPrincipal);
		when(jwtTokenProvider.generateToken(any())).thenReturn("token");

		authService.register(req);
		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());
		assertEquals("$2a$10$bcryptHash", captor.getValue().getPassword());
	}

	@Test
	void testRegister_SendsRegistrationEmail() {
		RegisterRequest req = new RegisterRequest("dave@example.com", "Strong@123", "Dave");
		when(userRepository.existsByEmail(anyString())).thenReturn(false);
		when(passwordEncoder.encode(anyString())).thenReturn("hash");
		when(userRepository.save(any(User.class))).thenReturn(localUser);
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userPrincipal);
		when(jwtTokenProvider.generateToken(any())).thenReturn("token");
		authService.register(req);
		verify(emailService, times(1)).sendRegistrationEmail(anyString(), anyString());
	}

	// =========================================================================
	// login
	// =========================================================================

	@Test
	void testLogin_ValidCredentials_ReturnsToken() {
		AuthRequest req = new AuthRequest("alice@example.com", "Strong@123");
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userPrincipal);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt.login.token");

		AuthResponse response = authService.login(req);
		assertNotNull(response);
		assertEquals("jwt.login.token", response.getAccessToken());
	}

	@Test
	void testLogin_InvalidCredentials_ThrowsUnauthorized() {
		AuthRequest req = new AuthRequest("alice@example.com", "WrongPwd@1");
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));
		ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
	}

	@Test
	void testLogin_ValidCredentials_SendsLoginEmail() {
		AuthRequest req = new AuthRequest("alice@example.com", "Strong@123");
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userPrincipal);
		when(jwtTokenProvider.generateToken(any())).thenReturn("token");
		authService.login(req);
		verify(emailService, times(1)).sendLoginNotificationEmail("alice@example.com");
	}

	@Test
	void testLogin_InvalidCredentials_DoesNotSendEmail() {
		AuthRequest req = new AuthRequest("alice@example.com", "WrongPwd@1");
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad"));
		assertThrows(ResponseStatusException.class, () -> authService.login(req));
		verify(emailService, never()).sendLoginNotificationEmail(anyString());
	}

	// =========================================================================
	// forgotPassword (now requires verified OTP)
	// =========================================================================

	@Test
	void testForgotPassword_WithVerifiedOtp_ReturnsSuccess() {
		ForgotPasswordRequest req = new ForgotPasswordRequest("NewStrong@123");
		OtpToken verified = OtpToken.builder().email("alice@example.com").otpHash("hash")
				.expiresAt(LocalDateTime.now().plusMinutes(10)).verified(true).build();

		when(otpTokenRepository.findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc("alice@example.com"))
				.thenReturn(Optional.of(verified));
		when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(localUser));
		when(passwordEncoder.encode("NewStrong@123")).thenReturn("$2a$10$newHash");

		MessageResponse response = authService.forgotPassword("alice@example.com", req);
		assertNotNull(response);
		assertEquals("Password has been changed successfully!", response.getMessage());
		verify(otpTokenRepository).deleteAllByEmail("alice@example.com");
	}

	@Test
	void testForgotPassword_WithoutVerifiedOtp_ThrowsForbidden() {
		ForgotPasswordRequest req = new ForgotPasswordRequest("NewStrong@123");
		when(otpTokenRepository.findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc("alice@example.com"))
				.thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> authService.forgotPassword("alice@example.com", req));
		assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
	}

	@Test
	void testForgotPassword_UnknownEmail_ThrowsNotFound() {
		ForgotPasswordRequest req = new ForgotPasswordRequest("NewStrong@123");
		OtpToken verified = OtpToken.builder().email("nobody@example.com").otpHash("hash")
				.expiresAt(LocalDateTime.now().plusMinutes(10)).verified(true).build();

		when(otpTokenRepository.findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc("nobody@example.com"))
				.thenReturn(Optional.of(verified));
		when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> authService.forgotPassword("nobody@example.com", req));
		assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
	}

	@Test
	void testForgotPassword_SendsConfirmationEmail() {
		ForgotPasswordRequest req = new ForgotPasswordRequest("NewStrong@123");
		OtpToken verified = OtpToken.builder().email("alice@example.com").otpHash("hash")
				.expiresAt(LocalDateTime.now().plusMinutes(10)).verified(true).build();

		when(otpTokenRepository.findTopByEmailAndVerifiedTrueOrderByCreatedAtDesc("alice@example.com"))
				.thenReturn(Optional.of(verified));
		when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(localUser));
		when(passwordEncoder.encode(anyString())).thenReturn("hash");

		authService.forgotPassword("alice@example.com", req);
		verify(emailService, times(1)).sendForgotPasswordEmail("alice@example.com");
	}

	// =========================================================================
	// sendOtp
	// =========================================================================

	@Test
	void testSendOtp_ExistingUser_ReturnsSuccess() {
		when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);
		when(otpTokenRepository.countByEmailAndCreatedAtAfter(anyString(), any())).thenReturn(0L);
		when(passwordEncoder.encode(anyString())).thenReturn("hashedOtp");

		MessageResponse response = authService.sendOtp("alice@example.com");
		assertNotNull(response);
		verify(otpTokenRepository).save(any(OtpToken.class));
		verify(emailService).sendOtpEmail(eq("alice@example.com"), anyString());
	}

	@Test
	void testSendOtp_RateLimited_ThrowsTooManyRequests() {
		when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);
		when(otpTokenRepository.countByEmailAndCreatedAtAfter(anyString(), any())).thenReturn(5L);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> authService.sendOtp("alice@example.com"));
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, ex.getStatusCode());
	}

	// =========================================================================
	// verifyOtp
	// =========================================================================

	@Test
	void testVerifyOtp_CorrectCode_MarksVerified() {
		OtpToken token = OtpToken.builder().email("alice@example.com").otpHash("hash")
				.expiresAt(LocalDateTime.now().plusMinutes(5)).attempts(0).verified(false).build();

		when(otpTokenRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc("alice@example.com"))
				.thenReturn(Optional.of(token));
		when(passwordEncoder.matches("123456", "hash")).thenReturn(true);

		MessageResponse response = authService.verifyOtp("alice@example.com", "123456");
		assertTrue(token.isVerified());
		verify(otpTokenRepository).save(token);
	}

	@Test
	void testVerifyOtp_WrongCode_IncrementsAttempts() {
		OtpToken token = OtpToken.builder().email("alice@example.com").otpHash("hash")
				.expiresAt(LocalDateTime.now().plusMinutes(5)).attempts(0).verified(false).build();

		when(otpTokenRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc("alice@example.com"))
				.thenReturn(Optional.of(token));
		when(passwordEncoder.matches("000000", "hash")).thenReturn(false);

		assertThrows(ResponseStatusException.class, () -> authService.verifyOtp("alice@example.com", "000000"));
		assertEquals(1, token.getAttempts());
	}

	// =========================================================================
	// resetPassword
	// =========================================================================

	@Test
	void testResetPassword_CorrectCurrentPassword_ReturnsSuccess() {
		when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(localUser));
		when(passwordEncoder.matches("Strong@123", "$2a$10$hashedPassword")).thenReturn(true);
		when(passwordEncoder.encode("NewStrong@456")).thenReturn("$2a$10$newHash");

		MessageResponse response = authService.resetPassword("alice@example.com", "Strong@123", "NewStrong@456");
		assertEquals("Password reset successfully!", response.getMessage());
	}

	@Test
	void testResetPassword_WrongCurrentPassword_ThrowsBadRequest() {
		when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(localUser));
		when(passwordEncoder.matches("WrongPwd@1", "$2a$10$hashedPassword")).thenReturn(false);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> authService.resetPassword("alice@example.com", "WrongPwd@1", "NewStrong@456"));
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
	}

	@Test
	void testResetPassword_SendsConfirmationEmail() {
		when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(localUser));
		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		when(passwordEncoder.encode(anyString())).thenReturn("hash");
		authService.resetPassword("alice@example.com", "Strong@123", "NewStrong@456");
		verify(emailService, times(1)).sendPasswordResetEmail("alice@example.com");
	}
}