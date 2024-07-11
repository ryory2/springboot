package com.example.demo.dto.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  @JsonProperty("first_name")
  @NotBlank(message = "firstname is mandatory")
  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must be alphanumeric")
  @Size(max = 255, message = "Username must be less than 255 characters")
  private String firstname;

  @JsonProperty("last_name")
  @NotBlank(message = "lastname is mandatory")
  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username must be alphanumeric")
  @Size(max = 255, message = "Username must be less than 255 characters")
  private String lastname;

  @NotBlank(message = "Mail is mandatory")
  @Email(message = "Mail should be a valid email address")
  @Size(max = 255, message = "Mail must be less than 255 characters")
  private String mail;

  @NotBlank(message = "Password is mandatory")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{1,255}$",
      message = "Password must be alphanumeric with symbols and less than 255 characters")
  private String password;
}
