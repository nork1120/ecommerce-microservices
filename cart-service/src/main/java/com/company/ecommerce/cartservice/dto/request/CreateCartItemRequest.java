package com.company.ecommerce.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartItemRequest {

    @NotNull(message = "productId 不可為空")
    private Long productId;

    @NotNull(message = "quantity 不可為空")
    @Min(value = 1,message = "數量不可小於1")
    private Integer quantity;


}
