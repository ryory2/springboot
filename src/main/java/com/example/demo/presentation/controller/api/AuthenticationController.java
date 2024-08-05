package com.example.demo.presentation.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.service.AuthenticationService;
import com.example.demo.dto.api.auth.AuthenticationRequest;
import com.example.demo.dto.api.auth.AuthenticationResponse;
import com.example.demo.dto.api.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService service;

  // ユーザ登録時のチェックをする
  @PostMapping("/register-validation")
  public ResponseEntity<Void> registCheck(@Validated({RegisterRequest.GroupOrder.class,
      RegisterRequest.PassGroupOrder.class}) @RequestBody RegisterRequest request) {
    service.registCheck(request);
    return ResponseEntity.noContent()
        .build();
  }

  // ユーザを登録し、otpを送信する
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @Validated({RegisterRequest.GroupOrder.class,
          RegisterRequest.PassGroupOrder.class}) @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }

  // 認証が完了したら、トークンを返却する
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @Validated({AuthenticationRequest.GroupOrder.class,
          AuthenticationRequest.PassGroupOrder.class}) @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  // 認証が完了したら、トークンを返却する
  @PostMapping("/logout")
  public ResponseEntity<AuthenticationResponse> logout(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.logout(request));
  }

  // トークンを受け取り、認証されているかをHTTPステータスコードで返却する
  @PostMapping("/jwt")
  // public ResponseEntity<AuthenticationResponse> authenticateJwt(
  // @RequestBody AuthenticationRequest request) {
  // return ResponseEntity.ok(service.logout(request));
  public ResponseEntity<Void> authenticateJwt() {
    return ResponseEntity.noContent()
        .build();
  }
}
