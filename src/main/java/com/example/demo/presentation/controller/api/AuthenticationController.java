package com.example.demo.presentation.controller.api;

import jakarta.validation.Valid;
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

  // ユーザを登録し、トークンを返却する
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @Validated({RegisterRequest.GroupOrder.class,
          RegisterRequest.PassGroupOrder.class}) @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }

  // 認証が完了したら、トークンを返却する
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @Valid @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  // 認証が完了したら、トークンを返却する
  @PostMapping("/logout")
  public ResponseEntity<AuthenticationResponse> logout(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.logout(request));
  }
}
