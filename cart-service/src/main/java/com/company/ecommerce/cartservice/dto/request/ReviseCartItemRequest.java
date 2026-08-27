package com.company.ecommerce.cartservice.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviseCartItemRequest {

    @NotNull
    private Long productId;

    @Min(value = 1,message = "數字不能小於1")
    private Integer quantity;

}
