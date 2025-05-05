package com.tbp.tbp_auth.controller;

import com.tbp.tbp_auth.datamodel.dao.User;
import com.tbp.tbp_auth.repository.UserRepository;
import com.tbp.tbp_auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TestAuthController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    void setupUser(String username) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPasswordHash("passwort");
        user.setRole("USER");
        user.setSteamId("0123456789");

        userRepository.save(user);
    }

    @Test
    void accessSecuredEndpoint_withValidToken_returnsOk() throws Exception {
        //given
        var username = "junitTestUser";
        setupUser(username);

        //when
        String token = jwtService.generateToken("junitTestUser"); // oder UserDetails

        //then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void accessSecuredEndpoint_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessSecuredEndpoint_withInvalidToken_returnsUnauthorized() throws Exception {

        var fakeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +  // valid base64 header
                ".eyJzdWIiOiJmYWtlVXNlciIsImV4cCI6MTcwMDAwMDAwMH0" + // valid base64 payload
                ".invalidsignature"; // invalid signature

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + fakeJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_withValidToken_returnsUserInfo() throws Exception {
        //given
        var username = "testuser";
        setupUser(username);

        //when
        var user = userRepository.findByUsername(username).orElseThrow();
        var token = jwtService.generateToken(user.getUsername());

        //then
        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(username + "@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
