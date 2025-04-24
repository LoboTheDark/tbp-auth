package com.tbp.tbp_auth.dto;

public record RegisterUserRequest(
        String username,
        String email,
        String password
) {}
