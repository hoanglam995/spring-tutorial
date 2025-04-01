package com.example.homework.utils;

import java.security.SecureRandom;

public class OTPUtil {
    private static final SecureRandom random = new SecureRandom();

    private OTPUtil() {
    }

    public static String generateOTP() {
        return String.format("%06d", random.nextInt(1_000_000));
    }
}
