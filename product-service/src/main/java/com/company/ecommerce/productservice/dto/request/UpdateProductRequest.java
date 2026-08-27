package com.company.ecommerce.productservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateProductRequest {


    private String name;

    private String brand;

    private String categoryId;

    private String description;

    @DecimalMin(value = "0.01", message = "價格不能低於0.01")
    private BigDecimal price;

    @Min(value = 0,message = "數量不能小於0")
    private Integer stock;

    private String mainImageUrl;

    private String status;

}
