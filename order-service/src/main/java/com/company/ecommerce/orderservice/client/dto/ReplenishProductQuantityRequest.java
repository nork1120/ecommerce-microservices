package com.company.ecommerce.orderservice.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReplenishProductQuantityRequest {

    /// 商品ID
    @NotNull
    private Long productId;

    /// 商品數量
    @NotNull
    @Min(1)
    private Integer quantity;

}
