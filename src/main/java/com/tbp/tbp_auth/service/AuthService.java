package com.tbp.tbp_auth.service;

import com.tbp.tbp_auth.dto.requests.LoginRequest;
import com.tbp.tbp_auth.dto.responses.JwtResponseDto;
import com.tbp.tbp_auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public JwtResponseDto authenticateAndGenerateToken(LoginRequest request) {
        var user = userRepository.findByUsername(request.username());
        if(user.isPresent())
        {
            var token = jwtService.generateToken(user.get().getUsername());
            return new JwtResponseDto(token, user.get().getSteamId());
        }

        user = userRepository.findByEmail(request.username());
        if(user.isPresent())
        {
            var token = jwtService.generateToken(user.get().getUsername());
            return new JwtResponseDto(token, user.get().getSteamId());
        }

        throw new UsernameNotFoundException("User not found");

    }


}
