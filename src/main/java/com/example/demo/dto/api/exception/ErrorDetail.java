package com.example.demo.dto.api.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// @JsonPropertyOrder({"status", "reason_phrase", "message", "timestamp", "path"})
public class ErrorDetail {
  private String common;
}
