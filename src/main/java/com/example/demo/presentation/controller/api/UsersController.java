package com.example.demo.presentation.controller.api;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.domain.model.Users;
import com.example.demo.domain.service.UsersService;
import com.example.demo.dto.api.request.UsersCreateRequest;
import com.example.demo.dto.api.request.UsersRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UsersController {
  @Autowired
  private UsersService usersService;

  @GetMapping
  public ResponseEntity<List<Users>> getAllUsers() {
    List<Users> users = usersService.getAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Users> getUserById(@PathVariable Long id) {
    Users users = usersService.getUserById(id);
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Users> postUsers(
      @Valid @RequestBody UsersCreateRequest usersCreateRequest) {
    Users createdUser = usersService.postUser(usersCreateRequest);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Users> updateUser(@PathVariable Long id,
      @RequestBody UsersRequest usersRequest) {
    Users updatedUser = usersService.updateUser(id, usersRequest);
    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    usersService.deleteByID(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT)
        .build();
  }

}
