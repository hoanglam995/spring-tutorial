package com.example.homework.messaging.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOTPMessage implements Serializable {
    private String phone;
    private String otp;

}
