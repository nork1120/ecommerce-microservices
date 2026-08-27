package com.company.ecommerce.userservice.controller;


import com.company.ecommerce.userservice.dto.requset.LoginRequest;
import com.company.ecommerce.userservice.dto.requset.RegisterUserRequest;
import com.company.ecommerce.userservice.dto.response.LoginResponse;
import com.company.ecommerce.userservice.dto.response.RegisterUserResponse;
import com.company.ecommerce.userservice.servicer.UserService;
import com.company.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/register",
            consumes = "application/json",
            produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        return ApiResponse.success("Registration successful", userService.registerUser(registerUserRequest), 201);
    }
    

    @PostMapping(value = "/login",
            consumes = "application/json",
            produces = "application/json")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success("Login successful", userService.login(loginRequest), 200);
    }

    @GetMapping(value = "/me")
    public ApiResponse<RegisterUserResponse> me(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success("Current user retrieved", userService.getCurrentUser(userId), 200);
    }
}
