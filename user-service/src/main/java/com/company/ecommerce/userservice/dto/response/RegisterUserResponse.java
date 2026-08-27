package com.company.ecommerce.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserResponse {

    private Long id;

    private String username;

    private String email;

    private String password;

}
