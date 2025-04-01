package com.example.homework.dtos.requests;

import com.example.homework.validations.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CheckRegisterOTPRequestDTO {
    @NotNull(message = "Số điện thoại không được để trống!")
    @ValidPhoneNumber
    private String phone;
    @NotNull(message = "OTP không được để trống!")
    @Pattern(regexp = "^\\d{6}$", message = "OTP không đúng định dạng!")
    private String otp;
}
