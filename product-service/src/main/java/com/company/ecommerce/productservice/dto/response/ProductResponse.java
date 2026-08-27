package com.company.ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

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


}
