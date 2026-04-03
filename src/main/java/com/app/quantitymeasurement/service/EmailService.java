package com.app.quantitymeasurement.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    /*
     * JavaMailSender is provided by Spring Boot
     * It is used to send emails using configured SMTP server
     */
    private final JavaMailSender mailSender;

    /*
     * Email address from which all mails will be sent
     * Value is injected from application.properties
     * Example: spring.mail.username=your-email@gmail.com
     */
    private final String fromAddress;

    /*
     * Constructor-based dependency injection
     * - Injects mailSender
     * - Injects email from application.properties
     */
    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    /*
     * =========================
     * SEND OTP EMAIL
     * =========================
     */
    @Async   // Runs in separate thread (non-blocking)
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            /*
             * SimpleMailMessage is a simple text-based email
             * (no HTML, no attachments)
             */
            SimpleMailMessage m = new SimpleMailMessage();

            // Set sender and receiver
            m.setFrom(fromAddress);
            m.setTo(toEmail);

            // Email subject
            m.setSubject("Your Verification Code - Quantity Measurement");

            /*
             * Email body
             * Includes OTP and expiry info
             */
            m.setText(
                    "Hi,\n\nYour verification code is: " + otp +
                    "\n\nThis code expires in 5 minutes.\n\nRegards,\nQuantity Measurement Team"
            );

            // Send email
            mailSender.send(m);

            // Log success
            log.info("OTP email sent to {}", toEmail);

        } catch (Exception ex) {

            // Log error if email fails
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
        }
    }

    /*
     * =========================
     * SEND REGISTRATION EMAIL
     * =========================
     */
    @Async
    public void sendRegistrationEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();

            m.setFrom(fromAddress);
            m.setTo(toEmail);

            // Welcome email subject
            m.setSubject("Welcome to Quantity Measurement App!");

            /*
             * Email body for new user
             */
            m.setText(
                    "Hi " + userName +
                    ",\n\nYour account has been created successfully.\n\nRegards,\nQuantity Measurement Team"
            );

            mailSender.send(m);

        } catch (Exception ex) {
            log.error("Failed to send registration email to {}: {}", toEmail, ex.getMessage());
        }
    }

    /*
     * =========================
     * SEND LOGIN NOTIFICATION EMAIL
     * =========================
     */
    @Async
    public void sendLoginNotificationEmail(String toEmail) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();

            m.setFrom(fromAddress);
            m.setTo(toEmail);

            // Security alert subject
            m.setSubject("New login to your Quantity Measurement account");

            /*
             * Email body warns user about login activity
             */
            m.setText(
                    "Hi,\n\nWe noticed a new login to your account." +
                    "\n\nIf this was not you, please reset your password." +
                    "\n\nRegards,\nQuantity Measurement Team"
            );

            mailSender.send(m);

        } catch (Exception ex) {
            log.error("Failed to send login email to {}: {}", toEmail, ex.getMessage());
        }
    }

    /*
     * =========================
     * SEND FORGOT PASSWORD EMAIL
     * =========================
     */
    @Async
    public void sendForgotPasswordEmail(String toEmail) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();

            m.setFrom(fromAddress);
            m.setTo(toEmail);

            // Password change confirmation
            m.setSubject("Your password has been changed");

            /*
             * Email body confirming password update
             */
            m.setText(
                    "Hi,\n\nYour password has been changed successfully." +
                    "\n\nRegards,\nQuantity Measurement Team"
            );

            mailSender.send(m);

        } catch (Exception ex) {
            log.error("Failed to send forgot-password email to {}: {}", toEmail, ex.getMessage());
        }
    }

    /*
     * =========================
     * SEND PASSWORD RESET EMAIL
     * =========================
     */
    @Async
    public void sendPasswordResetEmail(String toEmail) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();

            m.setFrom(fromAddress);
            m.setTo(toEmail);

            // Reset confirmation subject
            m.setSubject("Password reset successfully");

            /*
             * Email body confirming reset
             */
            m.setText(
                    "Hi,\n\nYour password has been reset successfully." +
                    "\n\nRegards,\nQuantity Measurement Team"
            );

            mailSender.send(m);

        } catch (Exception ex) {
            log.error("Failed to send password-reset email to {}: {}", toEmail, ex.getMessage());
        }
    }
}