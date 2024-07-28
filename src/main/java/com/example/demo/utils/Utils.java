package com.example.demo.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import com.example.demo.dto.api.CustomErrorResponse;

public class Utils {

  public static CustomErrorResponse createErrorResponse(HttpStatus status, String reasonPhrase,
      String message, HttpServletRequest request) {
    CustomErrorResponse errorResponse = new CustomErrorResponse();
    errorResponse.setStatus(status.value());
    errorResponse.setReasonPhrase(reasonPhrase);
    errorResponse.setMessage(message);
    errorResponse.setTimestamp(LocalDateTime.now()
        .format(DateTimeFormatter.ISO_DATE_TIME));
    errorResponse.setPath(request.getRequestURI());
    return errorResponse;
  }

}
