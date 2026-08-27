package com.company.ecommerce.cartservice.client;

import com.company.ecommerce.cartservice.dto.response.ProductResponse;
import com.company.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "product-service",
        url = "${services.product-service.url}"
)
public interface ProductClient {
    @GetMapping("/api/product/getProductInformation/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable("id") Long id);

    @GetMapping("/api/product/getProductInIds")
    ApiResponse<List<ProductResponse>> getProductsInIds(@RequestParam List<Long> ids);

}
