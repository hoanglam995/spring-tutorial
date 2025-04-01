package com.example.homework.utils;

import com.example.homework.dtos.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseUtil {
    private ResponseUtil() {
    }

    // Phương thức chung để tạo ResponseEntity
    private static <T> ResponseEntity<T> response(HttpStatus status, T body) {
        return ResponseEntity.status(status).body(body);
    }

    // Success với message
    public static <T> ResponseEntity<BaseResponse<T>> success(String message) {
        return response(HttpStatus.OK, new BaseResponse<>(HttpStatus.OK.value(), message, null, null));
    }

    // Success Response với dữ liệu
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return response(HttpStatus.OK, new BaseResponse<>(HttpStatus.OK.value(), null, data, null));
    }

    // Success Response với data và metadata
    public static <T> ResponseEntity<BaseResponse<T>> success(T data, Map<String, Object> metadata) {
        return response(HttpStatus.OK, new BaseResponse<>(HttpStatus.OK.value(), null, data, metadata));
    }

    // Error Response với message
    public static <T> ResponseEntity<BaseResponse<T>> error(HttpStatus status, String message) {
        return response(status, new BaseResponse<>(status.value(), message, null, null));
    }

    // Error Response với message và metadata
    public static <T> ResponseEntity<BaseResponse<T>> error(HttpStatus status, String message, Map<String, Object> metadata) {
        return response(status, new BaseResponse<>(status.value(), message, null, metadata));
    }
}
