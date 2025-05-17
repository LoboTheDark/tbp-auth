package com.tbp.tbp_auth.service;

import com.tbp.tbp_auth.datamodel.dao.AuthProvider;
import com.tbp.tbp_auth.datamodel.dao.User;
import com.tbp.tbp_auth.dto.requests.RegisterUserRequest;
import com.tbp.tbp_auth.dto.responses.UserResponseDto;
import com.tbp.tbp_auth.exception.UserAlreadyExistsException;
import com.tbp.tbp_auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${backend.syncSteamApiUrl}")
    private String backendSyncUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(RegisterUserRequest request) {
        duplicateCheck(request);
        var user = createNewUserObject(request);
        var savedUser = userRepository.save(user);

        callBackendToSync(savedUser.getSteamId());

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getSteamId(),
                savedUser.getProvider()
        );
    }

    private void callBackendToSync(String steamId) {


        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(backendSyncUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(steamId))
                    .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .join();
        }
    }


    private User createNewUserObject(RegisterUserRequest request) {
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setProvider(AuthProvider.LOCAL);
        user.setSteamId(request.steamId());
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
