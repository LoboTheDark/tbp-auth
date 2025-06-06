package com.tbp.tbp_auth.controller;

import com.tbp.tbp_auth.dto.requests.RegisterUserRequest;
import com.tbp.tbp_auth.dto.responses.UserResponseDto;
import com.tbp.tbp_auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody RegisterUserRequest request) {
        return userService.createUser(request);
    }
}
