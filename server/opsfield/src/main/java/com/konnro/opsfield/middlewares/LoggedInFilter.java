package com.konnro.opsfield.middlewares;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.konnro.opsfield.auth.JwtService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggedInFilter implements Filter {

  private final MiddlewareService middlewareService;
  private final JwtService jwtService;

  public LoggedInFilter(JwtService jwtService, MiddlewareService middlewareService) {
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
    if (!(token instanceof String)) {
      ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    chain.doFilter(request, response);
  }
}