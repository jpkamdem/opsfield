package com.konnro.opsfield.middlewares;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.konnro.opsfield.auth.JwtService;
import com.konnro.opsfield.errors.TeamNotFoundException;
import com.konnro.opsfield.errors.UserNotFoundException;
import com.konnro.opsfield.teams.Team;
import com.konnro.opsfield.teams.TeamRepository;
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

public class TeamFilter implements Filter {

  private final MiddlewareService middlewareService;
  private final JwtService jwtService;
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  public TeamFilter(MiddlewareService middlewareService, JwtService jwtService, TeamRepository teamRepository,
      UserRepository userRepository) {
    this.middlewareService = middlewareService;
    this.jwtService = jwtService;
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (middlewareService.isPathUnprotected(request)) {
      chain.doFilter(request, response);
      return;
    }

    if (!middlewareService.isTeamPath(request)) {
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
    User jtiUser = userRepository.findById(jti).orElseThrow(() -> new UserNotFoundException(jti));
    boolean isJtiUserAdmin = jtiUser.getRole().equals(Role.admin);
    if (isJtiUserAdmin) {
      chain.doFilter(request, response);
      return;
    }

    boolean isTeamWriteRequest = middlewareService.isTeamPath(request) && middlewareService.isWriteMethod(request);
    boolean isJtiUserManager = jtiUser.getRole().equals(Role.manager);

    if (isTeamWriteRequest) {
      if (!isJtiUserManager) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      chain.doFilter(request, response);
      return;
    }

    if (middlewareService.isReadMethod(request)) {
      Optional<UUID> maybePathId = middlewareService.uuidFromUri(request);
      if (maybePathId.isEmpty()) {
        if (!isJtiUserManager) {
          ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
          return;
        }

        chain.doFilter(request, response);
        return;
      }

      UUID pathId = maybePathId.get();
      Team pathTeam = teamRepository.findById(pathId).orElseThrow(() -> new TeamNotFoundException(pathId));
      boolean nonNullTeamId = jtiUser.getTeamId() != null;
      if (!nonNullTeamId) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      boolean workerOfPathTeam = jtiUser.getTeamId().equals(pathTeam.getId());
      if (!workerOfPathTeam) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }

      chain.doFilter(request, response);
      return;
    }
  }
}