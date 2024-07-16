package com.example.demo.domain.service;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.User;
import com.example.demo.domain.model.User.Role;
import com.example.demo.domain.repository.UserRepository;
import com.example.demo.dto.api.auth.AuthenticationRequest;
import com.example.demo.dto.api.auth.AuthenticationResponse;
import com.example.demo.dto.api.auth.RegisterRequest;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  @Value("${error.exception.badcredentials}")
  private String badCredentialsMessage;

  public AuthenticationResponse register(RegisterRequest request) {
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .mail(request.getMail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
    Optional<User> existingUser = userRepository.findByMail(user.getMail());
    if (existingUser.isPresent()) {
      throw new UserAlreadyExistsException(
          "User with email " + user.getMail() + " already exists.");
    }
    userRepository.save(user);
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  // カスタムの認証プロバイダー
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    String jwtToken = null;
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
      var user = userRepository.findByMail(request.getMail())
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      jwtToken = jwtService.generateToken(user);
    } catch (UserNotFoundException e) {
      throw new UserNotFoundException("No users found in the database");
    } catch (BadCredentialsException e) {
      e.printStackTrace();
      throw new BadCredentialsException(badCredentialsMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public AuthenticationResponse logout(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
    var user = userRepository.findByMail(request.getMail())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    var jwtToken = jwtService.revocateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }
}
