package com.company.ecommerce.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private Long id;

    private String spuId;

    private String skuCode;

    private String name;

    private String brand;

    private Long categoryId;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String mainImageUrl;

    private String status;

    private  Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
