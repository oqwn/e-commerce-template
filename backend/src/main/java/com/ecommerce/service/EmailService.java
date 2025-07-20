package com.ecommerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${app.email.from}")
    private String fromEmail;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    public void sendVerificationEmail(String toEmail, String firstName, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify Your Email - E-Commerce Store");
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;
            
            String text = String.format(
                "Hi %s,\n\n" +
                "Welcome to E-Commerce Store! Please click the link below to verify your email address:\n\n" +
                "%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't create an account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "E-Commerce Store Team",
                firstName != null ? firstName : "there",
                verificationUrl
            );
            
            message.setText(text);
            mailSender.send(message);
            
            logger.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.warn("Email service unavailable - skipping verification email for: {}", toEmail);
            logger.info("Verification token for {}: {}", toEmail, verificationToken);
            // Don't throw exception, just log and continue
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String firstName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset - E-Commerce Store");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            
            String text = String.format(
                "Hi %s,\n\n" +
                "You requested a password reset for your E-Commerce Store account.\n\n" +
                "Click the link below to reset your password:\n\n" +
                "%s\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "E-Commerce Store Team",
                firstName != null ? firstName : "there",
                resetUrl
            );
            
            message.setText(text);
            mailSender.send(message);
            
            logger.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.warn("Email service unavailable - skipping password reset email for: {}", toEmail);
            logger.info("Password reset token for {}: {}", toEmail, resetToken);
            // Don't throw exception, just log and continue
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to E-Commerce Store!");
            
            String text = String.format(
                "Hi %s,\n\n" +
                "Welcome to E-Commerce Store! Your email has been verified successfully.\n\n" +
                "You can now:\n" +
                "• Browse our products\n" +
                "• Add items to your cart\n" +
                "• Place orders\n" +
                "• Manage your profile\n\n" +
                "Visit our store: %s\n\n" +
                "Happy shopping!\n\n" +
                "Best regards,\n" +
                "E-Commerce Store Team",
                firstName != null ? firstName : "there",
                frontendUrl
            );
            
            message.setText(text);
            mailSender.send(message);
            
            logger.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", toEmail, e);
            // Don't throw exception for welcome email failures
        }
    }
}