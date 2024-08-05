package com.example.demo.presentation.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.service.OtpService;
import com.example.demo.dto.api.auth.SendOtpRequest;
import com.example.demo.dto.api.auth.SendOtpResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {
  private final OtpService otpService;

  // OTPを送信し、送信履歴 を返却する
  @PostMapping("/send")
  public ResponseEntity<SendOtpResponse> sendOtp(@Validated @RequestBody SendOtpRequest request) {
    return ResponseEntity.ok(otpService.sendOtp(request));
  }

}
