package com.example.demo.dto.api.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MyUserDetails implements UserDetails {

  private String username;
  private String password;
  private boolean active;
  private List<GrantedAuthority> authorities;

  // コンストラクタ
  // 役割: ユーザー情報の初期化
  // 実行タイミング: MyUserDetailsServiceでユーザー情報を取得した後
  public MyUserDetails(String username, String password, boolean active, String roles) {
    this.username = username;
    this.password = password;
    this.active = active;
    this.authorities = Arrays.stream(roles.split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  // ユーザーの権限を返すメソッド
  // 役割: ユーザーの権限情報の提供
  // 実行タイミング: Spring Securityが権限チェックを行う時
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  // ユーザーのパスワードを返すメソッド
  // 役割: 認証プロセスのためのパスワード提供
  // 実行タイミング: Spring Securityが認証を行う時
  @Override
  public String getPassword() {
    return password;
  }

  // ユーザー名を返すメソッド
  // 役割: 認証プロセスのためのユーザー名提供
  // 実行タイミング: Spring Securityが認証を行う時
  @Override
  public String getUsername() {
    return username;
  }

  // アカウントの有効期限が切れていないかを返すメソッド
  // 役割: アカウントの有効性チェック
  // 実行タイミング: Spring Securityが認証を行う時
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  // アカウントがロックされていないかを返すメソッド
  // 役割: アカウントのロック状態チェック
  // 実行タイミング: Spring Securityが認証を行う時
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  // 認証情報の有効期限が切れていないかを返すメソッド
  // 役割: 認証情報の有効性チェック
  // 実行タイミング: Spring Securityが認証を行う時
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  // アカウントが有効かどうかを返すメソッド
  // 役割: アカウントの有効性チェック
  // 実行タイミング: Spring Securityが認証を行う時
  @Override
  public boolean isEnabled() {
    return active;
  }
}
