package com.example.homework.services;

import com.example.homework.constants.UserStatus;
import com.example.homework.entities.UserEntity;
import com.example.homework.exceptions.ValidationException;
import com.example.homework.messaging.messages.RegisterOTPMessage;
import com.example.homework.messaging.producers.RegisterOTPProducer;
import com.example.homework.repositories.UserRepository;
import com.example.homework.utils.JwtUtil;
import com.example.homework.utils.OTPUtil;
import com.example.homework.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RegisterOTPProducer registerOTPProducer;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.cache.send-register-otp.max-count-otp}")
    private int maxCountOTP;

    @Value("${spring.cache.send-register-otp.max-count-incorrect-otp}")
    private int maxCountIncorrectOTP;
    public String register(String phone) {
        Optional<UserEntity> userOpt = this.userRepository.findByPhone(phone);
        if (userOpt.isPresent()) {
            if (Boolean.TRUE.equals(userOpt.get().getIsActivated())) {
                throw new ValidationException("SĐT đã được đăng ký!");
            }
            throw new ValidationException("SĐT đang chờ kích hoạt!");
        }

        UserEntity user = new UserEntity();
        user.setPhone(phone);
        user = this.userRepository.save(user);

        String otp = (String) this.redisService.get(RedisUtil.getKeyRegisterOTP(phone));
        if (otp == null) {
            otp = OTPUtil.generateOTP();
        }
        this.registerOTPProducer.sendOTP(new RegisterOTPMessage(user.getPhone(), otp));

        return otp;
    }

    public String resendOTP(String phone) {
        Optional<UserEntity> userOpt = this.userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            throw new ValidationException("SĐT chưa được đăng ký!");
        }

        UserEntity user = userOpt.get();
        if (Boolean.TRUE.equals(user.getIsActivated())) {
            throw new ValidationException("SĐT đã được đăng ký!");
        }

        String otp = (String) this.redisService.get(RedisUtil.getKeyRegisterOTP(phone));
        if (otp == null) {
            Integer countOtpInt = (Integer) this.redisService.get(RedisUtil.getKeyCountRegisterOTP(phone));
            if (countOtpInt != null && countOtpInt >= this.maxCountOTP) {
                throw new ValidationException("SĐT đã vợt quá số lần tạo OTP trong ngày!");
            }
            otp = OTPUtil.generateOTP();
        }
        Boolean lockSendOTP = (Boolean) this.redisService.get(RedisUtil.getKeyLockSendRegisterOTP(phone));
        if (lockSendOTP != null && lockSendOTP) {
            throw new ValidationException("Hãy thử lại sau " + this.redisService.getTTL(RedisUtil.getKeyLockSendRegisterOTP(phone)) + " giây");
        }
        this.registerOTPProducer.sendOTP(new RegisterOTPMessage(user.getPhone(), otp));

        return otp;
    }

    public String checkOTP(String phone, String otp) {
        Optional<UserEntity> userOpt = this.userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            throw new ValidationException("SĐT chưa được đăng ký!");
        }

        UserEntity user = userOpt.get();
        if (Boolean.TRUE.equals(user.getIsActivated())) {
            throw new ValidationException("SĐT đã được đăng ký!");
        }

        String oldOTP = (String) this.redisService.get(RedisUtil.getKeyRegisterOTP(phone));
        if (otp.equals(oldOTP)) {
            String token = JwtUtil.generateResetToken(phone);
            this.redisService.save(RedisUtil.getKeyChangePasswordToken(phone), token, 15, TimeUnit.MINUTES);
            return token;
        }
        String message = "Mã OTP không chính xác!";
        if (oldOTP == null) {
            message = "OTP đã hết hạn!";
        }
        Integer countIncorrectOTPInt = (Integer) this.redisService.get(RedisUtil.getKeyCountIncorrectRegisterOTP(phone));
        int countIncorrectOTP = countIncorrectOTPInt != null ? countIncorrectOTPInt + 1 : 1;
        if (countIncorrectOTP < maxCountIncorrectOTP) {
            this.redisService.save(RedisUtil.getKeyCountIncorrectRegisterOTP(phone), countIncorrectOTP);
            throw new ValidationException(message);
        }
        message = message + " Bạn đã nhập sai quá số lần cho phép, vui lòng tạo lại tài khoản!";
        this.redisService.delete(RedisUtil.getKeyRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyLockSendRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyCountIncorrectRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyChangePasswordToken(phone));
        this.userRepository.delete(user);
        throw new ValidationException(message);
    }

    public void resetPassword(String phone, String password, String token) {
        Optional<UserEntity> userOpt = this.userRepository.findByPhone(phone);
        if (userOpt.isEmpty()) {
            throw new ValidationException("SĐT chưa được đăng ký!");
        }

        UserEntity user = userOpt.get();
        if (Boolean.TRUE.equals(user.getIsActivated())) {
            throw new ValidationException("SĐT đã được đăng ký!");
        }

        String oldToken = (String) this.redisService.get(RedisUtil.getKeyChangePasswordToken(phone));
        if (oldToken == null) {
            throw new ValidationException("Token đã hết hạn!");
        }
        if (!JwtUtil.verifyToken(token)) {
            throw new ValidationException("Token không chính xác!");
        }

        user.setPassword(this.passwordEncoder.encode(password));
        user.setIsActivated(true);
        user.setStatus(UserStatus.ACTIVATED.getCode());
        this.userRepository.save(user);

        this.redisService.delete(RedisUtil.getKeyRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyLockSendRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyCountIncorrectRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyCountRegisterOTP(phone));
        this.redisService.delete(RedisUtil.getKeyChangePasswordToken(phone));
    }
}
