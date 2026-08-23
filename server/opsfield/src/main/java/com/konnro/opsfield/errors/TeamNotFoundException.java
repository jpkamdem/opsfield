package com.konnro.opsfield.errors;

import java.util.UUID;

public class TeamNotFoundException extends RuntimeException {
  public TeamNotFoundException(UUID id) {
    super("Équipe possédant l'ID " + id + " introuvable");
  }
}