package com.konnro.opsfield.errors;

import io.jsonwebtoken.JwtException;

public class CustomJwtException extends JwtException {
  public CustomJwtException() {
    super("Erreur lors de la gestion du token de connexion");
  }
}