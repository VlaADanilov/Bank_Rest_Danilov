package com.example.bankcards.advice;


import com.example.bankcards.exception.ServiceException;
import com.example.bankcards.exception.util.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<ErrorMessage> returnErrorMessage(ServiceException ex, WebRequest request) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(
                        ErrorMessage.builder()
                                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                                .statusCode(ex.getHttpStatus().value())
                                .message(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handlerBindException(BindException ex, WebRequest request) {
        ErrorMessage.ErrorMessageBuilder builder = ErrorMessage.builder()
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .statusCode(HttpStatus.BAD_REQUEST.value());
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            builder.errors(ex.getBindingResult().getFieldErrors().stream()
                    .filter(field -> field.getDefaultMessage() != null)
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
        } else if (ex.getGlobalError() != null) {
            return builder.message(ex.getGlobalError().getDefaultMessage()).build();
        }
        return builder.build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> exceptions = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            exceptions.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(exceptions, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> exceptions = new HashMap<>();
        ex.getConstraintViolations().forEach(cv -> {
            String fieldName = cv.getPropertyPath().toString();
            String errorMessage = cv.getMessage();
            exceptions.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(exceptions, HttpStatus.BAD_REQUEST);
    }
}
