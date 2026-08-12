package com.konnro.opsfield.auth;

import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.konnro.opsfield.errors.CustomJwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Service
public class JwtService {

  @Value("${app.secret}")
  private String baseKey;

  @Value("${app.duration}")
  @Getter
  private Long expirationDate;

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(baseKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generate(UUID id) {
    return Jwts.builder()
        .subject(id.toString())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expirationDate))
        .signWith(getSigningKey())
        .compact();
  }

  public Claims extractClaims(String token) throws CustomJwtException {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public String tokenValueFromHttp(ServletRequest request) throws NoSuchElementException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    Cookie[] cookies = httpServletRequest.getCookies();
    Stream<Cookie> stream = Objects.nonNull(cookies) ? Arrays.stream(cookies) : Stream.empty();
    String cookieValue = stream.filter(cookie -> "token".equals(cookie.getName()))
        .findFirst()
        .orElse(new Cookie("token", null))
        .getValue();

    return cookieValue;
  }
}