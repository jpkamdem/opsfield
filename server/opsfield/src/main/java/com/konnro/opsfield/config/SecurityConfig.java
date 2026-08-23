package com.konnro.opsfield.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.konnro.opsfield.middlewares.PermsFilter;
import com.konnro.opsfield.middlewares.TeamFilter;
import com.konnro.opsfield.teams.TeamRepository;
import com.konnro.opsfield.users.UserRepository;
import com.konnro.opsfield.auth.JwtService;
import com.konnro.opsfield.middlewares.LoggedInFilter;
import com.konnro.opsfield.middlewares.MiddlewareService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, LoggedInFilter loggedInFilter, PermsFilter permsFilter,
      TeamFilter teamFilter)
      throws Exception {
    return http
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .authorizeHttpRequests(requests -> requests

            // health
            .requestMatchers(HttpMethod.GET, "/health/ping").permitAll()

            // users
            .requestMatchers(HttpMethod.GET, "/api/users/").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/users/{id}").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").permitAll()

            // teams
            .requestMatchers(HttpMethod.GET, "/api/teams/").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/teams/{id}").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/teams/").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/teams/{id}").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/teams/{id}").permitAll()

            // auth
            .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()

            .anyRequest()
            .denyAll())

        .addFilterBefore(loggedInFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(permsFilter, LoggedInFilter.class)
        .addFilterAfter(teamFilter, PermsFilter.class)

        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public LoggedInFilter loggedInFilter(MiddlewareService middlewareService, JwtService jwtService) {
    return new LoggedInFilter(jwtService, middlewareService);
  }

  @Bean
  public TeamFilter teamFilter(MiddlewareService middlewareService, JwtService jwtService,
      TeamRepository teamRepository, UserRepository userRepository) {
    return new TeamFilter(middlewareService, jwtService, teamRepository, userRepository);
  }

  @Bean
  public PermsFilter permsFilter(MiddlewareService middlewareService, JwtService jwtService, UserRepository userRepository) {
    return new PermsFilter(userRepository, jwtService, middlewareService);
  }
}