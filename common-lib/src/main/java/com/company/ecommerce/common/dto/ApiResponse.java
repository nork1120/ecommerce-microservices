package com.company.ecommerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private int code;

    private String message;

    private T data;

    public static <T> ApiResponse<T> success(String message, T data, int code) {
        return new ApiResponse<>(
                true,
                code,
                message,
                data
        );
    }

    public static <T> ApiResponse<T> failure(String message, int code) {
        return new ApiResponse<>(
                false,
                code,
                message,
                null
        );
    }
}
