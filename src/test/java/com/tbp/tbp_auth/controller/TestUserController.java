package com.tbp.tbp_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbp.tbp_auth.datamodel.dao.AuthProvider;
import com.tbp.tbp_auth.dto.requests.RegisterUserRequest;
import com.tbp.tbp_auth.dto.responses.UserResponseDto;
import com.tbp.tbp_auth.exception.UserAlreadyExistsException;
import com.tbp.tbp_auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TestUserController {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        RegisterUserRequest request = new RegisterUserRequest("newUser", "user@example.com", "securePass", "0123456789");
        UserResponseDto user = new UserResponseDto(UUID.randomUUID(), "newUser", "user@example.com", "USER", "0123456789", AuthProvider.LOCAL);

        when(userService.createUser(any(RegisterUserRequest.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/api/auth/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))  // Add CSRF token for the request
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.steamId").value("0123456789"));
    }

    @Test
    @WithMockUser
    void shouldReturnConflictWhenEmailExists() throws Exception {
        // Given
        RegisterUserRequest request = new RegisterUserRequest("newUser", "existing@example.com", "pass", "0123456789");

        when(userService.createUser(any(RegisterUserRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Username already in use"));

        // When & Then
        mockMvc.perform(post("/api/auth/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))  // Add CSRF token for the request
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Username already in use")));
    }
}
