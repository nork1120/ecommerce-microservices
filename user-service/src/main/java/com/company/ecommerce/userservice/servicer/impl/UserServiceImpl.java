package com.company.ecommerce.userservice.servicer.impl;

import com.company.ecommerce.userservice.dto.requset.LoginRequest;
import com.company.ecommerce.userservice.dto.requset.RegisterUserRequest;
import com.company.ecommerce.userservice.dto.response.LoginResponse;
import com.company.ecommerce.userservice.dto.response.RegisterUserResponse;
import com.company.ecommerce.userservice.mapper.user.UserMapper;
import com.company.ecommerce.userservice.model.User;
import com.company.ecommerce.userservice.security.JwtTokenGenerator;
import com.company.ecommerce.userservice.servicer.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtTokenGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
        User registerEmail = userMapper.findByEmail(registerUserRequest.getEmail());

        if (registerEmail != null) {
            throw new IllegalArgumentException("Email 已被註冊");
        }

        User registerUsername = userMapper.findByUsername(registerUserRequest.getUsername());
        if (registerUsername != null) {
            throw new IllegalArgumentException("Username 已被使用");
        }

        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setUsername(registerUserRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setStatus("AVERAGE");
        userMapper.insert(user);

        return new RegisterUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userMapper.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("Email 或密碼錯誤");
        }

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new RuntimeException("Email 或密碼錯誤");
        }

        if (user.getStatus().equals("blockade")) {
            throw new RuntimeException("使用者已被停權");
        }

        String token = jwtTokenGenerator.generateToken(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setTokenType("Bearer");

        return loginResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public RegisterUserResponse getCurrentUser(Long userId) {
        User user = userMapper.findById(userId);

        if (user == null) {
            throw new RuntimeException("使用者不存在");
        }

        return new RegisterUserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPasswordHash());
    }

}
