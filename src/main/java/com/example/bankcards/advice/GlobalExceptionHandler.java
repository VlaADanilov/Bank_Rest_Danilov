package com.example.bankcards.advice;


import com.example.bankcards.entity.validation.Violation;
import com.example.bankcards.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final DefaultErrorAttributes errorAttributes;

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> onConstraintValidationException(
            HttpServletRequest request,
            ConstraintViolationException ex
    ) {
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(
                        violation -> {
                            Violation v = new Violation();
                            v.setMessage(violation.getMessage());
                            String[] s = violation.getPropertyPath().toString().split("\\.");
                            v.setFieldName(s[s.length - 1]);
                            return v;
                        }
                ).toList();
        return createJsonResponse(request, HttpStatus.BAD_REQUEST, null, violations);
    }
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, Object>> exception(HttpServletRequest request,ServiceException e) {
        return createJsonResponse(request, e.getHttpStatus(), e.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> createJsonResponse(
            HttpServletRequest httpRequest,
            HttpStatus status,
            String message,
            List<Violation> violations
    ) {
        WebRequest request = new ServletWebRequest(httpRequest);
        Map<String, Object> errorAttr = this.errorAttributes.getErrorAttributes(
                request,
                ErrorAttributeOptions.defaults()
        );
        errorAttr.put("status", status.value());
        errorAttr.put("error", status.getReasonPhrase());
        if (message != null) {
            errorAttr.put("message", message);
        }
        errorAttr.put("path", httpRequest.getRequestURI());
        if (violations != null && !violations.isEmpty()) {
            errorAttr.put("violations", violations);
        }
        return ResponseEntity.status(status).body(errorAttr);
    }
}
