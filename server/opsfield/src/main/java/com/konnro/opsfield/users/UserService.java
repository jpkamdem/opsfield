package com.konnro.opsfield.users;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.konnro.opsfield.errors.UserNotFoundException;

@Service
public class UserService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<User> index() {
    List<User> users = repository.findAll();
    return users;
  }

  public User show(UUID id) throws NoSuchElementException {
    User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    return user;
  }

  public void store(User newUser) throws IllegalArgumentException {
    User user = new User();
    user.setFirstname(newUser.getFirstname());
    user.setLastname(newUser.getLastname());
    user.setEmail(newUser.getEmail());
    user.setPassword(passwordEncoder.encode(newUser.getPassword()));
    user.setAge(newUser.getAge());
    user.setPhone_number(newUser.getPhone_number());
    user.setTeam_id(newUser.getTeam_id());
    user.setRole(newUser.getRole());
    user.setStatus(newUser.getStatus());
    repository.save(user);
  }

  public void update(User user, UUID id) throws NoSuchElementException {
    User foundUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    foundUser.setFirstname(user.getFirstname());
    foundUser.setLastname(user.getLastname());
    foundUser.setEmail(user.getEmail());
    foundUser.setPassword(passwordEncoder.encode(user.getPassword()));
    foundUser.setAge(user.getAge());
    foundUser.setPhone_number(user.getPhone_number());
    foundUser.setTeam_id(user.getTeam_id());
    foundUser.setRole(user.getRole());
    foundUser.setStatus(user.getStatus());
    repository.save(foundUser);
  }

  public void delete(UUID id) {
    User user = this.show(id);
    repository.delete(user);
  }
}