package com.example.demo.dto.api.auth;

import lombok.Data;

@Data
public class SuccessResponse {
  private String message;

  public SuccessResponse(String message) {
    this.message = message;
  }

  // getters and setters
}
