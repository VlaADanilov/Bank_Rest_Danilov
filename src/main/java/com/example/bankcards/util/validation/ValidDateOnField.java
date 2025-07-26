package com.example.bankcards.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidDateValidatorOnField.class)
@Documented
public @interface ValidDateOnField {
    String message() default "Дата, которую вы ввели, не превышает нынешнюю";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
