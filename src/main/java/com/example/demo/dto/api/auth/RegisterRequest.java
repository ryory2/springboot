package com.example.demo.dto.api.auth;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  // 入力チェック順序を制御するためのインタフェース
  @GroupSequence({ValidGroup1.class, ValidGroup2.class, ValidGroup3.class})
  public static interface GroupOrder {
  }

  public static interface ValidGroup1 {
  }

  public static interface ValidGroup2 {
  }

  public static interface ValidGroup3 {
  }

  // GroupOrderを複数設定するとどちらか一方が反応しない
  @GroupSequence({PassValidGroup1.class, PassValidGroup2.class, PassValidGroup3.class})
  public static interface PassGroupOrder {
  }

  public static interface PassValidGroup1 {
  }

  public static interface PassValidGroup2 {
  }

  public static interface PassValidGroup3 {
  }
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

  @NotBlank(message = "メールアドレスを入力してください。", groups = ValidGroup1.class)
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください。", groups = ValidGroup2.class)
  @Email(message = "有効なメールアドレスを入力してください。", groups = ValidGroup3.class)
  private String mail;

  @NotBlank(message = "パスワードを入力してください。", groups = PassValidGroup1.class)
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{1,255}$",
      message = "パスワードは英数字と記号を含み、255文字以内で入力してください。", groups = PassValidGroup2.class)
  private String password;
}
