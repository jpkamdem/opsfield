package com.konnro.opsfield.auth;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.konnro.opsfield.users.User;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
  private final JwtService jwtService;

  public AuthService(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  public Integer getExpiration() {
    return jwtService.getExpirationDate();
  }

  public String generateToken(UUID id) {
    return jwtService.generate(id);
  }

  public Object extract(String token) {
    return jwtService.extractClaims(token);
  }

  public void addJwtCookie(User user, HttpServletResponse response) {
    response.setHeader("Set-Cookie",
        "token=" + generateToken(user.getId()) + "; Path=/; HttpOnly; Secure; SameSite=Lax; Partitioned");
  }

  public void invalidateJwtCookie(HttpServletResponse response) {
    response.setHeader("Set-Cookie", "token=; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Path=/");
  }
}