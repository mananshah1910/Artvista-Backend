package com.artgallery.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(fromEmail, "ArtVista"));
            helper.setTo(toEmail);
            helper.setSubject("ArtVista - Password Reset OTP");
            helper.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP is valid for 2 minutes.");
            mailSender.send(message);
            log.info("✅ OTP email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("❌ CRITICAL EMAIL FAILURE to {}: {}. From: {}, Port: {}", toEmail, e.getMessage(), fromEmail, 465, e);
            throw new RuntimeException("Email delivery failed: " + e.getMessage());
        }
    }

    // @Async
    public void sendNotificationEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(fromEmail, "ArtVista"));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);
            mailSender.send(message);
            log.info("✅ Notification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("❌ CRITICAL NOTIFICATION FAILURE to {}: {}. From: {}, Port: {}", toEmail, e.getMessage(), fromEmail, 465, e);
            throw new RuntimeException("Notification delivery failed: " + e.getMessage());
        }
    }
}
