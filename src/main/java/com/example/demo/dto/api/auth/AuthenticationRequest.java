package com.example.demo.dto.api.auth;

import jakarta.validation.GroupSequence;
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
public class AuthenticationRequest {
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

  @NotBlank(message = "メールアドレスを入力してください。", groups = ValidGroup1.class)
  @Size(max = 255, message = "メールアドレスは255文字以内で入力してください。", groups = ValidGroup2.class)
  @Pattern(regexp = "^[a-zA-Z0-9._%+-@]*$", message = "メールアドレスは英数字で入力してください。",
      groups = ValidGroup2.class)
  private String mail;

  // @NotBlank(message = "パスワードを入力してください。", groups = PassValidGroup1.class)
  @NotBlank(message = "{validation.password}を入力してください。", groups = PassValidGroup1.class)
  @Size(max = 255, message = "パスワードは255文字以内で入力してください。", groups = PassValidGroup2.class)
  @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>\\/?]*$",
      message = "パスワードは英数字で入力してください。", groups = PassValidGroup2.class)
  String password;
}
