package com.example.demo.domain.service;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.User;
import com.example.demo.domain.model.User.Role;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.dto.api.request.UsersCreateRequest;
import com.example.demo.dto.api.request.UsersRequest;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  public List<User> getAllUsers() {
    log.info("処理開始： {}", getClass());
    List<User> users = userRepository.findAll();

    if (users.isEmpty()) {
      log.info("処理終了： {}", getClass());
      throw new UserNotFoundException("No users found in the database");
    }
    log.info("処理終了： {}", getClass());
    return users;
  }

  public User getUserById(Long id) {
    log.info("処理開始： {}", getClass());
    return userRepository.findById(id)
        .map(user -> {
          log.info("処理終了： {}", getClass());
          return user;
        })
        .orElseThrow(() -> {
          log.info("処理終了： {}", getClass());
          return new UserNotFoundException(id);
        });
  }

  public User getUserByMail(UsersRequest usersRequest) {
    log.info("処理開始： {}", getClass());
    Optional<User> result = userRepository.findByMail(usersRequest.getMail());
    log.info("処理終了： {}", getClass());
    return result.orElseThrow(() -> {
      log.info("処理終了：[失敗：NotFound]： {}", getClass());
      return new UserNotFoundException(usersRequest.getMail());
    });
  }

  public User postUser(UsersCreateRequest usersCreateRequest) {
    log.info("処理開始： {}", getClass());
    // ユーザ名が既に存在するかチェック
    if (userRepository.existsByMail(usersCreateRequest.getMail())) {
      log.info("処理終了：[失敗：コンフリクト]： {}", getClass());
      throw new UserAlreadyExistsException(
          "Username '" + usersCreateRequest.getMail() + "' is already taken.");
    }
    User user = User.builder()
        .firstname(usersCreateRequest.getFirstname())
        .lastname(usersCreateRequest.getLastname())
        .mail(usersCreateRequest.getMail())
        .password(passwordEncoder.encode(usersCreateRequest.getPassword()))
        .role(Role.USER)
        .build();
    if (!StringUtils.isEmpty(usersCreateRequest.getFirstname())) {
      user.setFirstname(usersCreateRequest.getFirstname());
    }
    if (!StringUtils.isEmpty(usersCreateRequest.getLastname())) {
      user.setLastname(usersCreateRequest.getLastname());
    }
    user.setMail(usersCreateRequest.getMail());
    user.setPassword(usersCreateRequest.getPassword());
    User result = userRepository.save(user);
    log.info("[成功]：" + result.toString());
    log.info("処理終了： {}", getClass());
    return result;
  }

  public User updateUser(Long id, UsersRequest usersRequest) {
    log.info("処理開始： {}", getClass());
    return userRepository.findById(id)
        .map(user -> {
          user.setFirstname(usersRequest.getFirstname());
          user.setLastname(usersRequest.getLastname());
          user.setPassword(usersRequest.getPassword());
          user.setMail(usersRequest.getMail());
          User updatedUser = userRepository.save(user);
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
    Optional<User> optionalUser = userRepository.findById(id);

    // Optionalが値を持っている場合の処理
    optionalUser.ifPresent(user -> {
      userRepository.deleteById(id);
      log.info("[成功]：ユーザ ID {} を削除しました", id);
    });

    // Optionalが空の場合の処理
    if (optionalUser.isEmpty()) {
      log.warn("[失敗]：ユーザ ID {} が見つかりませんでした", id);
      throw new UserNotFoundException(id);
    }

    log.info("処理終了： {}", getClass());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // TODO 自動生成されたメソッド・スタブ
    return null;
  }

}
