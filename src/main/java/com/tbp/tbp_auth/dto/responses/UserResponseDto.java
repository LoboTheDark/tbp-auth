package com.tbp.tbp_auth.dto.responses;

import com.tbp.tbp_auth.datamodel.dao.AuthProvider;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String username,
        String email,
        String role,
        String steamId,
        AuthProvider provider

) {}
