package com.company.ecommerce.userservice.dto.requset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "username 不可為空")
    @Size(min = 8, max = 15, message = "username 長度需為 8~15 字")
    private String username;

    @NotBlank(message = "email 不可為空")
    @Email(message = "email 格式錯誤")
    private String email;

    @NotBlank(message = "password 不可為空")
    @Size(min = 3, max = 20, message = "password 長度需為 3~20 字")
    private String password;

}
