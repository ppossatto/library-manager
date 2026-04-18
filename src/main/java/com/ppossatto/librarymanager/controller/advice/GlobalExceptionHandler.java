package com.ppossatto.librarymanager.controller.advice;

import com.ppossatto.librarymanager.exception.CoreException;
import com.ppossatto.librarymanager.exception.enums.CoreExceptionType;
import com.ppossatto.librarymanager.exception.enums.SeverityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
     MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult()
       .getFieldErrors()
       .forEach(error ->
          errors.put(error.getField(),
             error.getDefaultMessage()));

    Map<String, Object> responseError = Map.of(
       "errorCode", "ERR-99864",
       "severity", SeverityType.WARNING.name(),
       "fields", errors
    );

    return ResponseEntity.badRequest().body(responseError);
  }

  @ExceptionHandler(CoreException.class)
  public ResponseEntity<Map<String, String>> handleCoreException(CoreException ex) {
    log.error(ex.getMessage(), ex);
    CoreExceptionType exceptionType = ex.getExceptionType();
    Map<String, String> errorResponse = Map.of(
       "errorCode", exceptionType.getErrorCode(),
       "severity", exceptionType.getSeverityType().name(),
       "details", ex.getMessage()
    );
    return ResponseEntity.status(exceptionType.getStatus()).body(errorResponse);
  }
}
