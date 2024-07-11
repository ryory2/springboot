package com.example.demo.dto.api.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UsersRequest {

  @JsonProperty("first_name")
  private String firstname;

  @JsonProperty("last_name")
  private String lastname;

  private String password;

  private String mail;

}
