package com.konnro.opsfield.users;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {

  @Id
  @Getter
  @GeneratedValue
  @Column(nullable = false, unique = true)
  private UUID id;

  @Getter
  @Setter
  @Column(nullable = false, length = 55)
  private String firstname;

  @Getter
  @Setter
  @Column(nullable = false, length = 55)
  private String lastname;

  @Getter
  @Setter
  @Email(message = "Invalid email", regexp = "[a-zA-Z0-9_-]{3,}@[a-zA-Z0-9_-]{5,}.[a-z]{2,}")
  @Column(nullable = false, unique = true)
  private String email;

  @Getter
  @Setter
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=[^a-zA-Z0-9_]).{12,}$", message = "Password must contains 1 lower & upper case letter, 1 special char and at least 12 characters")
  @Column(nullable = false)
  private String password;

  @Getter
  @Setter
  @Column(nullable = false)
  private Integer age;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false)
  private Role role;

  @Getter
  @Setter
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false)
  private Status status;

  @Getter
  @Setter
  @Pattern(regexp = "0[6|7][0-9]{8}")
  @Column(nullable = false, unique = true)
  private String phone_number;

  @Getter
  @Setter
  @Column
  private UUID team_id;

  @CreationTimestamp
  @Getter
  @Column(nullable = false)
  private Timestamp created_at;

  @CreationTimestamp
  @Getter
  @Setter
  @Column(nullable = false)
  private Timestamp updated_at;
}