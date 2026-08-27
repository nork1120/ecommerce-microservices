package com.company.ecommerce.cartservice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CartItem {

    private Long id;

    private Long cartId;

    private Long productId;

    private String skuCode;

    private String productName;

    private String mainImageUrl;
    /// 價格快照
    private BigDecimal unitPrice;
    /// 數量
    private Integer quantity;
    /// 是否勾選 1否 2是
    private Integer selected;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
