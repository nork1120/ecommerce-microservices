package com.company.ecommerce.userservice.servicer.impl;

import com.company.ecommerce.userservice.dto.requset.LoginRequest;
import com.company.ecommerce.userservice.dto.requset.RegisterUserRequest;
import com.company.ecommerce.userservice.dto.response.LoginResponse;
import com.company.ecommerce.userservice.dto.response.RegisterUserResponse;
import com.company.ecommerce.userservice.mapper.user.UserMapper;
import com.company.ecommerce.userservice.model.User;
import com.company.ecommerce.userservice.security.JwtTokenGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUserCreatesUserWhenEmailAndUsernameAreAvailable() {
        RegisterUserRequest request = registerRequest();
        when(userMapper.findByEmail(request.getEmail())).thenReturn(null);
        when(userMapper.findByUsername(request.getUsername())).thenReturn(null);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });
        
        RegisterUserResponse response = userService.registerUser(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser01");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getPassword()).isEqualTo("encoded-password");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(userCaptor.capture());
        User insertedUser = userCaptor.getValue();
        assertThat(insertedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(insertedUser.getUsername()).isEqualTo("testuser01");
        assertThat(insertedUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(insertedUser.getStatus()).isEqualTo("AVERAGE");
    }

    @Test
    void registerUserThrowsWhenEmailAlreadyExists() {
        RegisterUserRequest request = registerRequest();
        when(userMapper.findByEmail(request.getEmail())).thenReturn(user(1L, "other", request.getEmail(), "hash", "AVERAGE"));

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email 已被註冊");

        verify(userMapper, never()).findByUsername(any());
        verify(userMapper, never()).insert(any());
        verifyNoInteractions(passwordEncoder, jwtTokenGenerator);
    }

    @Test
    void registerUserThrowsWhenUsernameAlreadyExists() {
        RegisterUserRequest request = registerRequest();
        when(userMapper.findByEmail(request.getEmail())).thenReturn(null);
        when(userMapper.findByUsername(request.getUsername())).thenReturn(user(1L, request.getUsername(), "other@example.com", "hash", "AVERAGE"));

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username 已被使用");

        verify(userMapper, never()).insert(any());
        verifyNoInteractions(passwordEncoder, jwtTokenGenerator);
    }

    @Test
    void loginReturnsTokenWhenCredentialsAreValid() {
        LoginRequest request = loginRequest();
        User user = user(1L, "testuser01", request.getEmail(), "encoded-password", "AVERAGE");
        when(userMapper.findByEmail(request.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtTokenGenerator.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = userService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void loginThrowsWhenEmailDoesNotExist() {
        LoginRequest request = loginRequest();
        when(userMapper.findByEmail(request.getEmail())).thenReturn(null);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email 或密碼錯誤");

        verifyNoInteractions(passwordEncoder, jwtTokenGenerator);
    }

    @Test
    void loginThrowsWhenPasswordDoesNotMatch() {
        LoginRequest request = loginRequest();
        User user = user(1L, "testuser01", request.getEmail(), "encoded-password", "AVERAGE");
        when(userMapper.findByEmail(request.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email 或密碼錯誤");

        verifyNoInteractions(jwtTokenGenerator);
    }

    @Test
    void loginThrowsWhenUserIsBlocked() {
        LoginRequest request = loginRequest();
        User user = user(1L, "testuser01", request.getEmail(), "encoded-password", "blockade");
        when(userMapper.findByEmail(request.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("使用者已被停權");

        verifyNoInteractions(jwtTokenGenerator);
    }

    @Test
    void getCurrentUserReturnsUserFromDatabase() {
        User principal = new User();
        principal.setId(1L);
        User user = user(1L, "testuser01", "test@example.com", "encoded-password", "AVERAGE");
        when(userMapper.findById(1L)).thenReturn(user);

        RegisterUserResponse response = userService.getCurrentUser(principal.getId());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser01");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void getCurrentUserThrowsWhenUserDoesNotExist() {
        User principal = new User();
        principal.setId(1L);
        when(userMapper.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> userService.getCurrentUser(principal.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("使用者不存在");
    }

    private RegisterUserRequest registerRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("testuser01");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        return request;
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        return request;
    }

    private User user(Long id, String username, String email, String passwordHash, String status) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setStatus(status);
        return user;
    }

}
