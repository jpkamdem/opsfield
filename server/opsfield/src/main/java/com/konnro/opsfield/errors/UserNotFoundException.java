package com.konnro.opsfield.errors;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(UUID id) {
    super("Compte possédant l'ID " + id + " introuvable");
  }
}