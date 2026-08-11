package com.konnro.opsfield.users;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/")
  ResponseEntity<?> index() {
    List<User> users = userService.index();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  ResponseEntity<?> show(@PathVariable UUID id) throws NoSuchElementException {
    User foundUser = userService.show(id);
    return new ResponseEntity<>(foundUser, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  ResponseEntity<?> store(@RequestBody @Valid User user, @PathVariable UUID id) throws NoSuchElementException {
    userService.update(user, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<?> delete(@PathVariable UUID id) throws NoSuchElementException {
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}