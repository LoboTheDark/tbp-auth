package com.tbp.tbp_auth.controller;

import com.tbp.tbp_auth.dto.requests.LoginRequest;
import com.tbp.tbp_auth.dto.responses.JwtResponseDto;
import com.tbp.tbp_auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequest request) {
        String token = authService.authenticateAndGenerateToken(request);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }
}
