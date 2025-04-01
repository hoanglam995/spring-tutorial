package com.example.homework.dtos.requests;

import com.example.homework.validations.annotations.StrongPassword;
import com.example.homework.validations.annotations.ValidPhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotNull(message = "Số điện thoại không được để trống!")
    @ValidPhoneNumber
    private String phone;
    @NotNull(message = "Token reset password không được để trống!")
    private String token;

    @NotNull(message = "Password không được để trống!")
    @StrongPassword
    private String password;
}
