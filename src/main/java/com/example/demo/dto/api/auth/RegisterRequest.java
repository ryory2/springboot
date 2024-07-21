package com.example.demo.dto.api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.example.demo.validation.First;
import com.example.demo.validation.Second;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  // @JsonProperty("first_name")
  // @NotBlank(message = "firstname is mandatory")
  // @Pattern(regexp = "^[\\p{L} \\-\\.。、・]*$",
  // message = "名前は日本語（漢字、ひらがな、カタカナ）、スペース、ハイフン、中黒を含むことができます")
  // @Size(max = 255, message = "Username must be less than 255 characters")
  // private String firstname;
  //
  // @JsonProperty("last_name")
  // @NotBlank(message = "lastname is mandatory")
  // @Pattern(regexp = "^[\\p{L} \\-\\.。、・]*$",
  // message = "名字は日本語（漢字、ひらがな、カタカナ）、スペース、ハイフン、中黒を含むことができます")
  // @Size(max = 255, message = "Username must be less than 255 characters")
  // private String lastname;

  @NotBlank(message = "メールアドレスを入力してください。", groups = First.class)
  @Email(message = "有効なメールアドレスを入力してください。", groups = Second.class)
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください。", groups = Second.class)
  private String mail;

  @NotBlank(message = "パスワードを入力してください。", groups = First.class)
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{1,255}$",
      message = "パスワードは英数字と記号を含み、255文字以内で入力してください。", groups = Second.class)
  private String password;
}
