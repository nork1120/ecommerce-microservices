package com.company.ecommerce.userservice.dto.requset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "請輸入 email")
    @Email(message = "Email 格式錯誤")
    private String email;

    @NotBlank(message = "請輸入 password")
    private String password;

}
