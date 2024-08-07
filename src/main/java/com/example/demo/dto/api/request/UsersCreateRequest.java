package com.example.demo.dto.api.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsersCreateRequest {

  @JsonProperty("first_name")
  @Pattern(regexp = "^[\\p{L} \\-\\.。、・]*$",
      message = "名前は日本語（漢字、ひらがな、カタカナ）、スペース、ハイフン、中黒を含むことができます")
  @Size(max = 255, message = "Username must be less than 255 characters")
  private String firstname;

  @JsonProperty("last_name")
  @Pattern(regexp = "^[\\p{L} \\-\\.。、・]*$",
      message = "名字は日本語（漢字、ひらがな、カタカナ）、スペース、ハイフン、中黒を含むことができます")
  @Size(max = 255, message = "Username must be less than 255 characters")
  private String lastname;

  @NotBlank(message = "Password is mandatory")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{1,255}$",
      message = "Password must be alphanumeric with symbols and less than 255 characters")
  private String password;

  @NotBlank(message = "Mail is mandatory")
  @Email(message = "Mail should be a valid email address")
  @Size(max = 255, message = "Mail must be less than 255 characters")
  private String mail;

}
