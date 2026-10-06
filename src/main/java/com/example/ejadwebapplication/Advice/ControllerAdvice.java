package com.example.ejadwebapplication.Advice;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.Api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvice {

    // Ours
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> apiException(ApiException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    // مثل تكرار قيمة unique أو حذف سجل مرتبط بسجلات ثانية
    // DB validation errors
    // & Constraint Exception
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> dataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(400).body(new ApiResponse("Operation violates data constraints"));
    }

    // JSON غلط أو ناقص
    // parse Exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(400).body(new ApiResponse("Invalid request body"));
    }

    // مثل إرسال نص مكان id في الرابط
    // TypesMisMatch Exception
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(400).body(new ApiResponse("Invalid value for parameter: " + e.getName()));
    }

    // Method not allowed Exception
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(405).body(new ApiResponse(e.getMessage()));
    }

    //wrong url
    @ExceptionHandler(value= NoResourceFoundException.class)
    public ResponseEntity<?> ApiException(NoResourceFoundException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }
    //wrong parameters (validation) -> returns only the message written in the Model/DTO
    // Server Validation Exception
    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public ResponseEntity<?> ApiException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getFieldError().getDefaultMessage()));
    }

    // Server Validation Exception
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> ConstraintViolationException(ConstraintViolationException e) {

        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    // SQL Constraint Ex:(Duplicate) Exception
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiResponse> SQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    // wrong write SQL in @column Exception
    @ExceptionHandler(value = InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ApiResponse> InvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> RuntimeException(RuntimeException e) {
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }
}
