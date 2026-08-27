package com.company.ecommerce.userservice.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    private String tokenType;

}
