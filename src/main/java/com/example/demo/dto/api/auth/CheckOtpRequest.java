package com.example.demo.dto.api.auth;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckOtpRequest {
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

  @NotBlank(message = "認証コードを入力してください。", groups = ValidGroup1.class)
  @Pattern(regexp = "^[0-9]{6}$", message = "認証コードは6桁の半角数字で入力してください。", groups = ValidGroup2.class)
  private String code;

}
