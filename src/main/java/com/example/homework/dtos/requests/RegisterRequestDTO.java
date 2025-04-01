package com.example.homework.dtos.requests;

import com.example.homework.validations.annotations.ValidPhoneNumber;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @ValidPhoneNumber
    private String phone;
}
