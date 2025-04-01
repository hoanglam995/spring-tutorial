package com.example.homework.validations.annotations;

import com.example.homework.validations.validators.ValidPhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPhoneNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "SĐT không đúng định dạng 11 số và bắt đầu bằng 84!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
