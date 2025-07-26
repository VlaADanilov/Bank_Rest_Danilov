package com.example.bankcards.util.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ValidDateValidatorOnField implements ConstraintValidator<ValidDateOnField, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        if (date == null) {
            return true;
        }

        LocalDate currentDate = LocalDate.now();
        return date.isAfter(currentDate);
    }
}
