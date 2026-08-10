package com.konnro.opsfield.users;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  public Optional<User> findByEmail(String email);

  public Optional<User> findByPhoneNumber(String phoneNumber);
}