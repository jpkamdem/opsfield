package com.konnro.opsfield.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
/**
 * UserServiceTest
 */
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @Test
  void storeShouldEncodePasswordAndSaveUser() {
    User user = new User();

    user.setFirstname("John");
    user.setLastname("Doe");
    user.setEmail("john@doe.com");
    user.setPassword("JohnDoe123***");
    user.setAge(18);
    user.setPhoneNumber("0755555555");
    user.setRole(Role.worker);
    user.setStatus(Status.available);

    when(passwordEncoder.encode("JohnDoe123***"))
        .thenReturn("HASHED_PASSWORD");

    userService.store(user);

    verify(passwordEncoder)
        .encode("JohnDoe123***");

    verify(userRepository)
        .save(any(User.class));
  }

  @Test
  void showEmailShouldReturnUser() {
    User user = new User();

    user.setEmail("john@doe.com");

    when(userRepository.findByEmail("john@doe.com"))
        .thenReturn(Optional.of(user));

    Optional<User> result = userService.showEmail("john@doe.com");

    assertTrue(result.isPresent());
    assertEquals("john@doe.com", result.get().getEmail());

    verify(userRepository)
        .findByEmail("john@doe.com");
  }
}