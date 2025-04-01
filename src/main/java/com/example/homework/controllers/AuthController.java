package com.example.homework.controllers;

import com.example.homework.dtos.BaseResponse;
import com.example.homework.dtos.requests.CheckRegisterOTPRequestDTO;
import com.example.homework.dtos.requests.RegisterRequestDTO;
import com.example.homework.dtos.requests.ResetPasswordRequestDTO;
import com.example.homework.services.AuthService;
import com.example.homework.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Map<String, String>>> register(@Valid @RequestBody RegisterRequestDTO request) {
        String otp = this.authService.register(request.getPhone());
        Map<String, String> response = new HashMap<>();
        response.put("otp", otp);
        return ResponseUtil.success(response);
    }

    @PostMapping("/otp/resend")
    public ResponseEntity<BaseResponse<Map<String, String>>> resendOTP(@Valid @RequestBody RegisterRequestDTO request) {
        String otp = this.authService.resendOTP(request.getPhone());
        Map<String, String> response = new HashMap<>();
        response.put("otp", otp);
        return ResponseUtil.success(response);
    }

    @PostMapping("/otp/check")
    public ResponseEntity<BaseResponse<Map<String, String>>> checkOTP(@Valid @RequestBody CheckRegisterOTPRequestDTO request) {
        String token = this.authService.checkOTP(request.getPhone(), request.getOtp());
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseUtil.success(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        this.authService.resetPassword(request.getPhone(), request.getPassword(), request.getToken());
        return ResponseUtil.success("Thành công");
    }
}
