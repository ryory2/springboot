package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleGeneralException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .build();
    // .body("An error occurred: " + e.getMessage());
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(e.getMessage());
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<Void> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errors);
  }
}
