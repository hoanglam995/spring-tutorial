package com.example.homework.validations.annotations;

import com.example.homework.validations.validators.StrongPasswordValidator;
import com.example.homework.validations.validators.ValidPhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Mật khẩu phải có ít nhất 8 ký tự, có chữa ít nhất 1 chữ viết hoa, 1 chữ viết thường, 1 số và 1 ký tự đặc biệt";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
