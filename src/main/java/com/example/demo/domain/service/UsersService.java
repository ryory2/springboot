package com.example.demo.domain.service;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.Users;
import com.example.demo.domain.repository.UsersRepository;
import com.example.demo.dto.api.request.UsersRequest;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UsersService {
  @Autowired
  private UsersRepository usersRepository;

  public List<Users> getAllUsers() {
    log.info("処理開始： {}", getClass());
    List<Users> users = usersRepository.findAll();

    if (users.isEmpty()) {
      log.info("処理終了： {}", getClass());
      throw new UserNotFoundException("No users found in the database");
    }
    log.info("処理終了： {}", getClass());
    return users;
  }

  public Users getUserById(Long id) {
    log.info("処理開始： {}", getClass());
    return usersRepository.findById(id)
        .map(user -> {
          log.info("処理終了： {}", getClass());
          return user;
        })
        .orElseThrow(() -> {
          log.info("処理終了： {}", getClass());
          return new UserNotFoundException(id);
        });
  }

  public Users getUserByMail(UsersRequest usersRequest) {
    log.info("処理開始： {}", getClass());
    Users result = usersRepository.findByMail(usersRequest.getMail());
    log.info("処理終了： {}", getClass());
    return result;
  }

  public Users postUser(UsersRequest usersRequest) {
    log.info("処理開始： {}", getClass());
    // ユーザ名が既に存在するかチェック
    if (usersRepository.existsByMail(usersRequest.getMail())) {
      log.info("処理終了：[失敗：コンフリクト]： {}", getClass());
      throw new UserAlreadyExistsException(
          "Username '" + usersRequest.getMail() + "' is already taken.");
    }
    Users users = new Users();
    if (!StringUtils.isEmpty(usersRequest.getUserName())) {
      users.setUserName(usersRequest.getUserName());
    }
    users.setMail(usersRequest.getMail());
    users.setPassword(usersRequest.getPassword());
    Users result = usersRepository.save(users);
    log.info("[成功]：" + result.toString());
    log.info("処理終了： {}", getClass());
    return result;
  }

  public Users updateUser(Long id, UsersRequest usersRequest) {
    log.info("処理開始： {}", getClass());
    return usersRepository.findById(id)
        .map(user -> {
          user.setUserName(usersRequest.getUserName());
          user.setPassword(usersRequest.getPassword());
          user.setMail(usersRequest.getMail());
          Users updatedUser = usersRepository.save(user);
          log.info("[成功]：");
          log.info("処理終了：[成功：{}]：{}", updatedUser.toString(), getClass());
          return updatedUser;
        })
        .orElseThrow(() -> {
          log.info("処理終了：[失敗：NotFound]： {}", getClass());
          return new UserNotFoundException(id);
        });
  }

  public void deleteByID(Long id) {
    log.info("処理開始： {}", getClass());
    // findByIdでOptional<User>を取得
    Optional<Users> optionalUser = usersRepository.findById(id);

    // Optionalが値を持っている場合の処理
    optionalUser.ifPresent(user -> {
      usersRepository.deleteById(id);
      log.info("[成功]：ユーザ ID {} を削除しました", id);
    });

    // Optionalが空の場合の処理
    if (optionalUser.isEmpty()) {
      log.warn("[失敗]：ユーザ ID {} が見つかりませんでした", id);
      throw new UserNotFoundException(id);
    }

    log.info("処理終了： {}", getClass());
  }
}
