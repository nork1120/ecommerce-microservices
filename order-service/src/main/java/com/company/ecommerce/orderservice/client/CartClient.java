package com.company.ecommerce.orderservice.client;


import com.company.ecommerce.orderservice.client.dto.CartResponse;
import com.company.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service", url = "${services.cart-service.url}")
public interface CartClient {

    @GetMapping("/api/cart/getCartCheckout")
    ApiResponse<CartResponse> getCartCheckout(@RequestHeader("X-User-Id") Long userId);

    @DeleteMapping("/api/cart/removeItem/{id}")
    ApiResponse<Void> removeItem(@RequestHeader("X-User-Id") Long userId,
                                 @PathVariable("id") Long id);

    @DeleteMapping("/api/cart/deleteCartSelectedItem")
    ApiResponse<Void> deleteCartSelectedItem(@RequestHeader("X-User-Id") Long userId);


}
