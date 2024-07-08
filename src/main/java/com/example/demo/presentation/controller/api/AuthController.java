package com.example.demo.presentation.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.service.MyUserDetailsService;
import com.example.demo.dto.api.auth.AuthenticationRequest;
import com.example.demo.dto.api.auth.AuthenticationResponse;
import com.example.demo.utils.JwtUtil;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private MyUserDetailsService userDetailsService;

  // 認証リクエストを処理するエンドポイント
  // 役割: ユーザー認証とJWTトークン生成
  // 実行タイミング: クライアントが認証リクエストを送信したとき
  @PostMapping("/login")
  public ResponseEntity<?> createAuthenticationToken(
      @RequestBody AuthenticationRequest authenticationRequest) throws Exception {
    try {
      // ユーザー名とパスワードで認証を試みる
      // 役割: 提供された認証情報の検証
      // 実行タイミング: 認証リクエスト処理時
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
          authenticationRequest.getUsername(), authenticationRequest.getPassword()));
    } catch (BadCredentialsException e) {
      // 認証失敗時の例外処理
      // 役割: 認証失敗の通知
      // 実行タイミング: 認証が失敗したとき
      throw new Exception("Incorrect username or password", e);
    }

    // ユーザー詳細の取得
    // 役割: 認証されたユーザーの詳細情報の取得
    // 実行タイミング: 認証成功後
    final UserDetails userDetails =
        userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

    // JWTトークンの生成
    // 役割: 認証されたユーザーのJWTトークン生成
    // 実行タイミング: ユーザー詳細取得後
    final String jwt = jwtUtil.generateToken(userDetails.getUsername());

    // JWTトークンをレスポンスとして返す
    // 役割: 生成されたトークンのクライアントへの送信
    // 実行タイミング: トークン生成後
    return ResponseEntity.ok(new AuthenticationResponse(jwt));
  }
}
