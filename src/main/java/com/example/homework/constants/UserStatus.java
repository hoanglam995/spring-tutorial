package com.example.homework.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus {
    NOT_ACTIVATED(0, "Chưa kích hoạt"),
    ACTIVATED(1, "Đang Hoạt động");

    private final int code;
    private final String value;

    public static String getValueByCode(int code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode() == code) {
                return status.getValue();
            }
        }
        return null;
    }
}
