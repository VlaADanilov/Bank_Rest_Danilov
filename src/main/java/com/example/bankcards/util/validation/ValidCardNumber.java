package com.example.bankcards.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = ValidCardNumberValidator.class)
@Documented
public @interface ValidCardNumber {
    String message() default "Номер карты должен состоять из 16 цифр, без лишних символов";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
