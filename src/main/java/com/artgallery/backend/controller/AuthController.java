package com.artgallery.backend.controller;

import com.artgallery.backend.model.User;
import com.artgallery.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.artgallery.backend.service.EmailService;
import com.artgallery.backend.service.OtpService;
import com.artgallery.backend.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("visitor");
        
        User savedUser = userRepository.save(user);

        String otp = otpService.generateOtp(savedUser.getEmail());
        System.out.println("\n========================================================");
        System.out.println("🔔 OTP FOR " + savedUser.getEmail() + " IS: " + otp);
        System.out.println("========================================================\n");
        
        try {
            emailService.sendNotificationEmail(
                java.util.Objects.requireNonNull(savedUser.getEmail()), 
                "Welcome to ArtVista! Verify your login", 
                "Hello " + savedUser.getName() + ",\n\nWelcome to ArtVista! We're thrilled to have you join our community.\n\nTo safely complete your first login, please use the following OTP:\n\n" + otp + "\n\nThis OTP is valid for 2 minutes.\n\nBest regards,\nThe ArtVista Team"
            );
        } catch (Exception e) {
            System.err.println("Failed to send welcome/otp email: " + e.getMessage());
        }
        
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("requiresFirstLoginOtp", true);
        responseMap.put("email", savedUser.getEmail());
        responseMap.put("message", "Registration successful. Please verify OTP sent to your email.");
        return ResponseEntity.status(202).body(responseMap);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User loggedInUser = userOpt.get();

            if (loggedInUser.isFirstLogin()) {
                String otp = otpService.generateOtp(loggedInUser.getEmail());
                System.out.println("\n========================================================");
                System.out.println("🔔 OTP FOR LOGIN " + loggedInUser.getEmail() + " IS: " + otp);
                System.out.println("========================================================\n");
                
                try {
                    emailService.sendNotificationEmail(
                        java.util.Objects.requireNonNull(loggedInUser.getEmail()), 
                        "Welcome to ArtVista! Verify your login", 
                        "Hello " + loggedInUser.getName() + ",\n\nWelcome to ArtVista! We're thrilled to have you join our community.\n\nTo safely complete your first login, please use the following OTP:\n\n" + otp + "\n\nThis OTP is valid for 2 minutes.\n\nBest regards,\nThe ArtVista Team"
                    );
                } catch (Exception e) {
                    System.err.println("Failed to send welcome/otp email: " + e.getMessage());
                }
                
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("requiresFirstLoginOtp", true);
                responseMap.put("email", loggedInUser.getEmail());
                responseMap.put("message", "First login requires OTP verification.");
                return ResponseEntity.status(202).body(responseMap);
            }

            String token = tokenProvider.generateToken(loggedInUser.getEmail(), loggedInUser.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedInUser);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/verify-first-login")
    public ResponseEntity<?> verifyFirstLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        if (!otpService.validateOtp(email, otp)) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Mark as no longer first login
            user.setFirstLogin(false);
            userRepository.save(user);

            String token = tokenProvider.generateToken(user.getEmail(), user.getRole());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body("User not found.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User with this email does not exist.");
        }

        String otp = otpService.generateOtp(email);
        System.out.println("\n========================================================");
        System.out.println("🔔 FORGOT PASSWORD OTP FOR " + email + " IS: " + otp);
        System.out.println("========================================================\n");
        
        try {
            emailService.sendOtpEmail(java.util.Objects.requireNonNull(email), otp);
            return ResponseEntity.ok("OTP sent to your email. It is valid for 2 minutes.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (!otpService.validateOtp(email, otp)) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Send notification
            try {
                emailService.sendNotificationEmail(java.util.Objects.requireNonNull(email), "Password Changed Successfully", "Hello " + user.getName() + ",\n\nYour account password has been successfully reset. If you did not make this change, please contact support immediately.");
            } catch (Exception e) {
                // Not critical if notification fails
            }
            
            return ResponseEntity.ok("Password successfully reset.");
        }

        return ResponseEntity.badRequest().body("User not found.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User with this email does not exist.");
        }

        User user = userOpt.get();
        String otp = otpService.generateOtp(email);
        System.out.println("\n========================================================");
        System.out.println("🔔 RESEND OTP FOR " + email + " IS: " + otp);
        System.out.println("========================================================\n");
        
        try {
            if (user.isFirstLogin()) {
                emailService.sendNotificationEmail(
                    java.util.Objects.requireNonNull(email), 
                    "Welcome to ArtVista! Verify your login", 
                    "Hello " + user.getName() + ",\n\nWelcome to ArtVista! We're thrilled to have you join our community.\n\nTo safely complete your first login, please use the following OTP:\n\n" + otp + "\n\nThis OTP is valid for 2 minutes.\n\nBest regards,\nThe ArtVista Team"
                );
            } else {
                emailService.sendOtpEmail(java.util.Objects.requireNonNull(email), otp);
            }
            return ResponseEntity.ok("OTP sent to your email. It is valid for 2 minutes.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        try {
            emailService.sendNotificationEmail(java.util.Objects.requireNonNull(email), "ArtVista SMTP Test", "This is a test email from your deployed backend.");
            return ResponseEntity.ok("Test email sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("SMTP Test Failed: " + e.getMessage());
        }
    }
}
