package com.tbp.tbp_auth.service;

import com.tbp.tbp_auth.datamodel.dao.AuthProvider;
import com.tbp.tbp_auth.datamodel.dao.User;
import com.tbp.tbp_auth.dto.requests.RegisterUserRequest;
import com.tbp.tbp_auth.dto.responses.UserResponseDto;
import com.tbp.tbp_auth.exception.UserAlreadyExistsException;
import com.tbp.tbp_auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(RegisterUserRequest request) {
        duplicateCheck(request);
        var user = createNewUserObject(request);
        var savedUser = userRepository.save(user);

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getProvider()
        );
    }

    private User createNewUserObject(RegisterUserRequest request) {
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setProvider(AuthProvider.LOCAL);
        user.setRole("USER");
        return user;
    }

    private void duplicateCheck(RegisterUserRequest request) {
        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("E-Mail already in use: " + request.email());
                });

        userRepository.findByUsername(request.username())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("Username already in use: " + request.username());
                });
    }
}
