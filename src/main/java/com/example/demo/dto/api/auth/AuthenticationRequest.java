package com.example.demo.dto.api.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationRequest  {

  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must be alphanumeric")
  @Size(max = 255, message = "Username must be less than 255 characters")
  private String username;

  @NotBlank(message = "Password is mandatory")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{1,255}$",
      message = "Password must be alphanumeric with symbols and less than 255 characters")
  private String password;
}
