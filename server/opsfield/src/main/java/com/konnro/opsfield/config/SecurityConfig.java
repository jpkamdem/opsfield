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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(csrf -> csrf.disable())
        .httpBasic(basic -> basic.disable())
        .formLogin(form -> form.disable())
        .authorizeHttpRequests(requests -> requests

            // health
            .requestMatchers(HttpMethod.GET, "/health/ping")
            .permitAll()

            // users
            .requestMatchers(HttpMethod.GET, "/api/users/")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/api/users/{id}")
            .permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/users/{id}")
            .permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/users/{id}")
            .permitAll()

            // auth
            .requestMatchers(HttpMethod.POST, "/api/auth/register")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/login")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/logout")
            .permitAll()

            .anyRequest()
            .denyAll())
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}