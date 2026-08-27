package com.company.ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductDetailsResponse {

    private String spuId;

    private List<ProductResponse>  products;

}
