package com.example.homework.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RedisUtil {
    public static String getKeyRegisterOTP(String phone) {
        return "register:" + phone + ":otp";
    }

    public static String getKeyLockSendRegisterOTP(String phone) {
        return "register:" + phone + ":lock_send_otp";
    }

    public static String getKeyCountRegisterOTP(String phone) {
        return "register:" + phone + ":" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ":count_otp";
    }

    public static String getKeyCountIncorrectRegisterOTP(String phone) {
        return "register:" + phone + ":count_incorrect_otp";
    }

    public static String getKeyChangePasswordToken(String phone) {
        return "register:" + phone + ":change_password_token";
    }
}
