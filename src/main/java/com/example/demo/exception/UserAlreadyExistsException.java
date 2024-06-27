package com.example.demo.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String massage) {
    super(massage);
  }
}
