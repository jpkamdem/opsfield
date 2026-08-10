package com.konnro.opsfield.auth;

import lombok.Getter;

@Getter
public class LoginCredentials {
  private String identifier;
  private String password;
}