package com.konnro.opsfield.teams;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teams")
public class Team {

  @Id
  @Getter
  @GeneratedValue
  @Column(nullable = false, unique = true)
  private UUID id;

  @Getter
  @Setter
  @Column(nullable = false, length = 55)
  private String name;
}