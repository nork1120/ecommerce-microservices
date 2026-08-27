package com.company.ecommerce.userservice.servicer;

import com.company.ecommerce.userservice.dto.requset.LoginRequest;
import com.company.ecommerce.userservice.dto.requset.RegisterUserRequest;
import com.company.ecommerce.userservice.dto.response.LoginResponse;
import com.company.ecommerce.userservice.dto.response.RegisterUserResponse;

public interface UserService {


    RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest);

    LoginResponse login(LoginRequest loginRequest);

    RegisterUserResponse getCurrentUser(Long userId);

}
