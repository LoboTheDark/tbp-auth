package com.tbp.tbp_auth.controller;

import com.tbp.tbp_auth.datamodel.dao.User;
import com.tbp.tbp_auth.dto.requests.LoginRequest;
import com.tbp.tbp_auth.dto.responses.JwtResponseDto;
import com.tbp.tbp_auth.dto.responses.UserResponseDto;
import com.tbp.tbp_auth.repository.UserRepository;
import com.tbp.tbp_auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequest request) {
        String token = authService.authenticateAndGenerateToken(request);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> optUser = userRepository.findByUsername(username); // Holt den User aus der DB
        if(optUser.isEmpty())
        {
            throw new UsernameNotFoundException("Could not find user for /me");
        }
        var user = optUser.get();
        return ResponseEntity.ok(new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getSteamId(),
                user.getProvider()
        ));
    }
}
