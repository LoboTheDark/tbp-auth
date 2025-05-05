package com.tbp.tbp_auth.dto.requests;

public record RegisterUserRequest(
        String username,
        String email,
        String password,
        String steamId
) {}
