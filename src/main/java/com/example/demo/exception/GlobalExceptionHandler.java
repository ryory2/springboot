package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import com.example.demo.dto.api.CustomErrorResponse;
import com.example.demo.dto.api.exception.ErrorDetail;
import com.example.demo.dto.api.exception.ErrorResponse;
import com.example.demo.utils.Utils;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<CustomErrorResponse> handleGeneralException(Exception e,
      HttpServletRequest request) {
    e.printStackTrace();
    CustomErrorResponse errorResponse = Utils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), request);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<CustomErrorResponse> handleUserNotFoundException(UserNotFoundException e,
      HttpServletRequest request) {
    CustomErrorResponse errorResponse = Utils.createErrorResponse(HttpStatus.CONFLICT,
        HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(), request);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<CustomErrorResponse> handleUserAlreadyExistsException(
      UserAlreadyExistsException e, HttpServletRequest request) {
    CustomErrorResponse errorResponse = Utils.createErrorResponse(HttpStatus.CONFLICT,
        HttpStatus.CONFLICT.getReasonPhrase(), e.getMessage(), request);
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e,
      HttpServletRequest request) {
    ErrorDetail errorDetail = new ErrorDetail(e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("error", errorDetail);

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(errorResponse);
  }

  @ExceptionHandler(SendTimesExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleSendTimesExceededException(
      SendTimesExceededException e, HttpServletRequest request) {
    ErrorDetail errorDetail = new ErrorDetail(e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse("error", errorDetail);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  // @ExceptionHandler(MethodArgumentNotValidException.class)
  // public ResponseEntity<Map<String, String>> handleValidationExceptions(
  // MethodArgumentNotValidException ex) {
  // Map<String, String> errors = new HashMap<>();
  // ex.getBindingResult()
  // .getFieldErrors()
  // .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
  // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
  // .body(errors);
  // }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "error");

    Map<String, String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.toMap(fieldError -> fieldError.getField(),
            fieldError -> fieldError.getDefaultMessage()));

    response.put("errors", errors);
    return ResponseEntity.badRequest()
        .body(response);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public Map<String, String> handleNoHandlerFoundException(HttpServletRequest request,
      NoHandlerFoundException ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put("error", "The requested resource was not found: " + request.getRequestURI());
    return errorResponse;
  }

}
