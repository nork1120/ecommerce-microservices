package com.company.ecommerce.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {

    @NotNull(message = "quantity 不得為空")
    @Min(value = 1,message = "quantity 不可小於1")
    private Integer quantity;

}
