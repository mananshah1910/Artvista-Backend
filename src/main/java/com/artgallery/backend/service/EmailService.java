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
    public void sendOtpEmail(@org.springframework.lang.NonNull String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress("mananshah1918@gmail.com", "ArtVista Gallery"));
            helper.setTo(toEmail);
            helper.setSubject("ArtVista - Password Reset OTP");
            
            String htmlBody = "<div style=\"font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 10px;\">"
                    + "<h2 style=\"color: #333;\">ArtVista</h2>"
                    + "<div style=\"font-size: 16px; color: #555; line-height: 1.5;\">"
                    + "Your OTP for password reset is: <strong style=\"font-size: 20px; color: #000;\">" + otp + "</strong><br><br>"
                    + "This OTP is valid for 2 minutes."
                    + "</div>"
                    + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\">"
                    + "<p style=\"font-size: 12px; color: #999;\">If you did not request this email, please ignore it.</p>"
                    + "</div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("✅ HTML OTP email dispatched to {}", toEmail);
        } catch (Exception e) {
            log.error("❌ CRITICAL EMAIL FAILURE to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Email delivery failed: " + e.getMessage());
        }
    }

    // @Async
    public void sendNotificationEmail(@org.springframework.lang.NonNull String toEmail, @org.springframework.lang.NonNull String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress("mananshah1918@gmail.com", "ArtVista Gallery"));
            helper.setTo(toEmail);
            helper.setSubject(subject);
            
            String htmlBody = "<div style=\"font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 10px;\">"
                    + "<h2 style=\"color: #333;\">ArtVista</h2>"
                    + "<div style=\"font-size: 16px; color: #555; line-height: 1.5;\">"
                    + body.replace("\n", "<br>")
                    + "</div>"
                    + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\">"
                    + "<p style=\"font-size: 12px; color: #999;\">If you did not request this email, please ignore it.</p>"
                    + "</div>";

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email successfully dispatched to {}", toEmail);
        } catch (Exception e) {
            log.error("❌ CRITICAL EMAIL FAILURE to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Email delivery failed: " + e.getMessage());
        }
    }
}
