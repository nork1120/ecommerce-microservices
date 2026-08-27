package com.company.ecommerce.productservice.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductStockAdjustment {

    private Long productId;

    private Integer quantity;
}
