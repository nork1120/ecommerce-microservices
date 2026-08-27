package com.company.ecommerce.cartservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {

    private Long id;

    private Long userId;

    private String status; ///'購物車狀態：ACTIVE, CHECKED_OUT'

    private LocalDateTime createdAt; ///創建時間

    private LocalDateTime updatedAt; ///更新時間

}
