package com.example.demo.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"status", "reason_phrase", "message", "timestamp", "path"})
public class CustomErrorResponse {
  private int status;
  @JsonProperty("reason_phrase")
  private String reasonPhrase;
  private String message;
  private String timestamp;
  private String path;
}
