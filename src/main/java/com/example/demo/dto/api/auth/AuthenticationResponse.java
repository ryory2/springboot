package com.example.demo.dto.api.auth;

import lombok.Data;

@Data
public class AuthenticationResponse {
  private final String jwt;
}
