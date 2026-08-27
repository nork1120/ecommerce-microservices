package com.company.ecommerce.cartservice.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;

    private Long productId;

    private String skuCode;

    private String productName;

    private String mainImageUrl;

    private BigDecimal unitPrice;

    private BigDecimal productPrice;

    private Integer quantity;

    private BigDecimal subTotal;

    private Integer selected;

}
