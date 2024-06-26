package com.example.demo.domain.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.Users;
import com.example.demo.domain.repository.UsersRepository;
import com.example.demo.dto.api.request.UsersRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UsersService {
  @Autowired
  private UsersRepository usersRepository;

  public List<Users> getAllUsers() {
    log.info("処理開始： {}", getClass());
    return usersRepository.findAll();
  }

  public Users getUserById(Long id) {
    return usersRepository.findById(id)
        .orElse(null);
  }

  public void postUser(UsersRequest usersRequest) {
    // Set creation timestamp
    Users users = new Users();
    if (!StringUtils.isEmpty(usersRequest.getUserName())) {
      users.setUserName(usersRequest.getUserName());
    }
    users.setMail(usersRequest.getMail());
    users.setPassword(usersRequest.getPassword());
    usersRepository.save(users);
  }

  public Users updateUser(Long id, UsersRequest usersRequest) {
    return usersRepository.findById(id)
        .map(users -> {
          users.setUserName(usersRequest.getUserName());
          users.setPassword(usersRequest.getPassword());
          users.setMail(usersRequest.getMail());
          return usersRepository.save(users);
        })
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public void deleteByID(Long id) {
    usersRepository.deleteById(id);
  }
}
