package com.tbp.tbp_auth.service;

import com.tbp.tbp_auth.datamodel.dao.User;
import com.tbp.tbp_auth.dto.requests.RegisterUserRequest;
import com.tbp.tbp_auth.exception.UserAlreadyExistsException;
import com.tbp.tbp_auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TestUserService {

    private static final Logger logger = LoggerFactory.getLogger(TestUserService.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        RegisterUserRequest request = new RegisterUserRequest("newUser", "user@example.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.username())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");

        User savedUser = new User(UUID.randomUUID(), request.email(), request.username());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        var result = userService.createUser(request);

        // Then
        logger.info("Request {} and Result {}", request.username(), result.username());
        assertEquals(request.username(), result.username());
        assertEquals(request.email(), result.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        RegisterUserRequest request = new RegisterUserRequest("newUser", "existing@example.com", "password123");
        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(mock(User.class)));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        RegisterUserRequest request = new RegisterUserRequest("existingUser", "user@example.com", "password123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(request.username()))
                .thenReturn(Optional.of(mock(User.class)));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }
}
