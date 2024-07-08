package com.example.demo.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.dto.api.auth.MyUserDetails;

// 「UserDetails」・・・Spring Securityにおいてユーザーの認証や認可を行うために必要なインターフェース
// 以下のメソッドを含む
// getUsername(): ユーザーのユーザー名を返すメソッド
// getPassword(): ユーザーのパスワードを返すメソッド
// getAuthorities(): ユーザーの権限を返すメソッド
// isEnabled(): ユーザーアカウントが有効かどうかを返すメソッド
// その他、ユーザーに関するさまざまな情報を提供するメソッド群
@Service
public class MyUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  // ユーザー名に基づいてUserDetailsを取得するメソッド
  // 役割: 認証プロセスのためのユーザー情報の提供
  // 実行タイミング: ユーザーがログインを試みる時、または認証が必要な時
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // User user = userRepository
    // .findByUsername(username);
    // if (user == null) {
    // throw new UsernameNotFoundException("User not found with username: " + username);
    // }
    // return new CustomUserDetails(user);

    // デモ用
    return new MyUserDetails("user", "password", true, "");
  }
}
