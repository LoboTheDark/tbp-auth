package com.tbp.tbp_auth.dto.requests;

public record LoginRequest(
        String username,
        String password
) {}

