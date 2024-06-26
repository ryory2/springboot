package com.example.demo.dto.api.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsersRequest {

  @JsonProperty("user_name")
  private String userName;

  private String password;

  private String mail;

}
