package com.konnro.opsfield.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konnro.opsfield.users.User;
import com.konnro.opsfield.users.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/register")
  ResponseEntity<?> register(@RequestBody @Valid User user) throws IllegalArgumentException {
    userService.store(user);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/login")
  void login() {
  }
}