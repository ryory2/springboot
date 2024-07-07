package com.example.demo.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.User;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.other.CustomUserDetails;

// 「UserDetails」・・・Spring Securityにおいてユーザーの認証や認可を行うために必要なインターフェース
// 以下のメソッドを含む
// getUsername(): ユーザーのユーザー名を返すメソッド
// getPassword(): ユーザーのパスワードを返すメソッド
// getAuthorities(): ユーザーの権限を返すメソッド
// isEnabled(): ユーザーアカウントが有効かどうかを返すメソッド
// その他、ユーザーに関するさまざまな情報を提供するメソッド群
@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository
        .findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }
    return new CustomUserDetails(user);
  }
}
