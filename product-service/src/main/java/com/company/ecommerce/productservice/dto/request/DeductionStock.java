package com.company.ecommerce.productservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;


@Data

public class DeductionStock {


    @NotBlank(message = "商品ID 不得為空")
    private Long id;

    @NotNull(message = "數量不得為空")
    @Min(value = 1, message = "數量不得小於1")
    private Integer quantity;

}
