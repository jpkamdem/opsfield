package com.konnro.opsfield.middlewares;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.konnro.opsfield.auth.JwtService;
import com.konnro.opsfield.errors.UserNotFoundException;
import com.konnro.opsfield.users.Role;
import com.konnro.opsfield.users.User;
import com.konnro.opsfield.users.UserRepository;

import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PermsFilter implements Filter {

  private final MiddlewareService middlewareService;
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public PermsFilter(UserRepository userRepository, JwtService jwtService, MiddlewareService middlewareService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.middlewareService = middlewareService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (middlewareService.isPathUnprotected(request)) {
      chain.doFilter(request, response);
      return;
    }

    String token = jwtService.tokenValueFromHttp(request);
    Claims decodedToken = jwtService.extractClaims(token);
    if (!(decodedToken instanceof Claims)) {
      ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }

    UUID jti = UUID.fromString(decodedToken.getSubject());
    User tokenUser = userRepository.findById(jti).orElseThrow(() -> new UserNotFoundException(jti));
    boolean isTokerUserAdmin = tokenUser.getRole().equals(Role.admin);
    if (isTokerUserAdmin) {
      chain.doFilter(request, response);
      return;
    }

    Optional<UUID> pathId = middlewareService.uuidFromUri(request);
    if (pathId.isEmpty()) {
      chain.doFilter(request, response);
      return;
    }

    UUID id = pathId.get();
    User pathUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    boolean isTokenUserManager = tokenUser.getRole().equals(Role.manager);
    if (isTokenUserManager) {
      boolean nonNullTeamIds = tokenUser.getTeamId() != null && pathUser.getTeamId() != null;
      if ((nonNullTeamIds)) {
        boolean areTeamIdsEqual = tokenUser.getTeamId().equals(pathUser.getTeamId());
        if (areTeamIdsEqual) {
          chain.doFilter(request, response);
          return;
        }
      }
    }

    boolean areTokenUserPathUserEqual = pathUser.getId().equals(tokenUser.getId());
    if (areTokenUserPathUserEqual) {
      chain.doFilter(request, response);
      return;
    }

    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}