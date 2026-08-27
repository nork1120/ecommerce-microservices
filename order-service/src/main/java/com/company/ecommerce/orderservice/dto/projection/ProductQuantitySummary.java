package com.company.ecommerce.orderservice.dto.projection;

import lombok.Data;

@Data
public class ProductQuantitySummary {

    private Long productId;

    private Integer quantity;
}
