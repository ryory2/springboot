package com.example.demo.exception;

public class SendTimesExceededException extends RuntimeException {
  public SendTimesExceededException(String message) {
    super(message);
  }
}
