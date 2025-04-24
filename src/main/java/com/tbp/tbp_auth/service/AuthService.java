package com.tbp.tbp_auth.service;

import com.tbp.tbp_auth.dto.requests.LoginRequest;
import com.tbp.tbp_auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String authenticateAndGenerateToken(LoginRequest request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return jwtService.generateToken(user.getUsername());
    }
}
