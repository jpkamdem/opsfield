package com.konnro.opsfield.middlewares;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.konnro.opsfield.auth.JwtService;
import com.konnro.opsfield.errors.UserNotFoundException;
import com.konnro.opsfield.users.User;
import com.konnro.opsfield.users.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Service
public class MiddlewareService {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Getter
  public List<String> unprotectedPaths = List.of("/api/auth/login", "/api/auth/register", "/api/auth/logout",
      "/health/ping");

  public MiddlewareService(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  public boolean isPathUnprotected(ServletRequest request) {
    HttpServletRequest HttpServletRequest = (HttpServletRequest) request;
    List<String> unprotectedPaths = getUnprotectedPaths();
    String path = HttpServletRequest.getRequestURI();
    return unprotectedPaths.contains(path);
  }

  public Optional<UUID> uuidFromUri(ServletRequest request) {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    String uri = httpServletRequest.getRequestURI();
    String lastPart = uri.substring(uri.lastIndexOf("/")).substring(1);
    try {
      return Optional.of(UUID.fromString(lastPart));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public User getUserFromCookie(ServletRequest request) {
    String cookieValue = jwtService.tokenValueFromHttp(request);
    Claims decodedToken = jwtService.extractClaims(cookieValue);
    UUID jti = UUID.fromString(decodedToken.getSubject());
    User jwtCookieUser = userRepository.findById(jti).orElseThrow(() -> new UserNotFoundException(jti));
    return jwtCookieUser;
  }
}
