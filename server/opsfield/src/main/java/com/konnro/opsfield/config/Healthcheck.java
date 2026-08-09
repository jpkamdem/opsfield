package com.konnro.opsfield.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class Healthcheck {

  @GetMapping("/ping")
  public ResponseEntity<?> check() {
    return new ResponseEntity<>(HttpStatus.OK);
  }
}