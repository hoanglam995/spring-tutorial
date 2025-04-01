package com.example.homework.services;

import com.example.homework.messaging.messages.RegisterOTPMessage;
import com.example.homework.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class MessageProcessingService {
    private final RedisService redisService;
    private final long lifeSeconds;
    private final long lockResendSeconds;

    public MessageProcessingService(RedisService redisService,
                                    @Value("${spring.cache.send-register-otp.life-otp-seconds}") long lifeSeconds,
                                    @Value("${spring.cache.send-register-otp.lock-resend-otp-seconds}") long lockResendSeconds) {
        this.redisService = redisService;
        this.lifeSeconds = lifeSeconds;
        this.lockResendSeconds = lockResendSeconds;
    }

    public void processSendRegisterOTP(RegisterOTPMessage message) {
        log.debug("Processing send phone: {} OTP: {}", message.getPhone(), message.getOtp());
        String oldOTP = (String) this.redisService.get(RedisUtil.getKeyRegisterOTP(message.getPhone()));
        if (oldOTP == null) {
            Integer countOtpInt = (Integer) this.redisService.get(RedisUtil.getKeyCountRegisterOTP(message.getPhone()));
            int countOtp = countOtpInt != null ? countOtpInt + 1 : 1;
            this.redisService.save(RedisUtil.getKeyRegisterOTP(message.getPhone()), message.getOtp(), this.lifeSeconds);
            this.redisService.save(RedisUtil.getKeyCountRegisterOTP(message.getPhone()), countOtp, 24, TimeUnit.HOURS);
        }
        this.redisService.save(RedisUtil.getKeyLockSendRegisterOTP(message.getPhone()), true, this.lockResendSeconds);
    }
}
