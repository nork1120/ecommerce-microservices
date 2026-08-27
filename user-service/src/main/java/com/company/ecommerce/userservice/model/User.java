package com.company.ecommerce.userservice.model;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class User {

    private Long id;

    private String username;

    private String email;

    private String passwordHash;

    private String status;

    private String role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
