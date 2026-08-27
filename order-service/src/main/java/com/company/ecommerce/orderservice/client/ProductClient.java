package com.company.ecommerce.orderservice.client;

import com.company.ecommerce.orderservice.client.dto.DeductionProductStock;
import com.company.ecommerce.orderservice.client.dto.ListReplenishProductQuantityRequest;
import com.company.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "product-service", url = "${services.product-service.url}")
public interface ProductClient {


    @PutMapping("/api/product/updateStock")
    ApiResponse<Integer> deductionProductStock(@RequestBody List<DeductionProductStock> deductionProductStock);

    /**
     * 補回商品數量
     * @param request
     * @return
     */
    @PutMapping("/api/product/replenishProduct")
    ApiResponse<Integer> setProductStockInProductIdIsQuantity(@Valid @RequestBody ListReplenishProductQuantityRequest request);

}
