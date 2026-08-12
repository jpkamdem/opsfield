package com.konnro.opsfield.auth;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konnro.opsfield.users.User;
import com.konnro.opsfield.users.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  private final AuthService authService;
  private final PasswordEncoder passwordEncoder;

  public AuthController(UserService userService, AuthService authService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.authService = authService;
  }

  @PostMapping("/register")
  ResponseEntity<?> register(@RequestBody @Valid User user, HttpServletResponse response)
      throws IllegalArgumentException {
    Optional<User> emailUser = userService.showEmail(user.getEmail());
    if (emailUser.isPresent()) {
      return new ResponseEntity<>("Cette adresse mail est déjà utilisée", HttpStatus.BAD_REQUEST);
    }

    Optional<User> phoneUser = userService.showPhone_Number(user.getPhoneNumber());
    if (phoneUser.isPresent()) {
      return new ResponseEntity<>("Ce numéro de téléphone est déjà utilisée", HttpStatus.BAD_REQUEST);
    }

    User newUser = userService.store(user);
    authService.addJwtCookie(newUser, response);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  ResponseEntity<?> login(@RequestBody @Valid LoginCredentials credentials, HttpServletResponse response) {
    Optional<User> emailUser = userService.showEmail(credentials.getIdentifier());
    if (emailUser.isEmpty()) {
      return new ResponseEntity<>("Aucun compte n'est associé à cette adresse mail", HttpStatus.BAD_REQUEST);
    }

    User user = emailUser.get();

    Boolean IsValidPassword = passwordEncoder.matches(credentials.getPassword(), user.getPassword());
    if (!IsValidPassword) {
      return new ResponseEntity<>("Mauvais mot de passe", HttpStatus.BAD_REQUEST);
    }

    authService.addJwtCookie(user, response);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/logout")
  ResponseEntity<?> logout(HttpServletResponse response) {
    authService.invalidateJwtCookie(response);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}