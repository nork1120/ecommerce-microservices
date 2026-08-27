package com.company.ecommerce.orderservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;

    private Long productId;

    private String skuCode;

    private String productName;

    private String mainImageUrl;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subTotal;

    private Integer selected;
}
