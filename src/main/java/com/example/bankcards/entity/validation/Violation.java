package com.example.bankcards.entity.validation;

import lombok.Data;

@Data
public class Violation {
    private String fieldName;
    private String message;
}
