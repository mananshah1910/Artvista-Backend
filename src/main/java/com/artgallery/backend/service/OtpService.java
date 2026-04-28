package com.artgallery.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // Store email -> OtpData
    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private static final long EXPIRE_MINS = 2;

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        long expirationTime = System.currentTimeMillis() + (EXPIRE_MINS * 60 * 1000);
        otpCache.put(email, new OtpData(otp, expirationTime));
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        OtpData otpData = otpCache.get(email);
        if (otpData == null) {
            return false;
        }
        
        if (System.currentTimeMillis() > otpData.getExpirationTime()) {
            otpCache.remove(email);
            return false; // OTP expired
        }
        
        if (otpData.getOtp().equals(otp)) {
            otpCache.remove(email); // Used correctly, clear it
            return true;
        }
        return false;
    }

    public void clearOtp(String email) {
        otpCache.remove(email);
    }

    private static class OtpData {
        private final String otp;
        private final long expirationTime;

        public OtpData(String otp, long expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public long getExpirationTime() {
            return expirationTime;
        }
    }
}
