package com.company.ecommerce.productservice.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "類別不得為空")
    private String supId;

    @NotBlank(message = "規格不得為空")
    private String skuCode;

    @NotBlank(message = "商品名稱不的為空")
    private String name;

    private String brand;

    private Long categoryId;

    private String description;

    @NotNull(message = "必須填價格")
    @DecimalMin(value = "0.01",message = "價格必須大於0")
    private BigDecimal price;

    @NotNull(message = "必須填數量")
    @Min(value = 0,message = "不可小於0")
    private Integer stock;

    private String mainImageUrl;

    private String status;


}
