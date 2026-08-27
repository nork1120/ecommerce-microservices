package com.company.ecommerce.orderservice.controller;


import com.company.ecommerce.orderservice.client.CartClient;
import com.company.ecommerce.orderservice.client.dto.CartResponse;
import com.company.ecommerce.common.dto.ApiResponse;
import com.company.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.company.ecommerce.orderservice.dto.request.PaymentRequest;
import com.company.ecommerce.orderservice.dto.response.OrderSelectListResponse;
import com.company.ecommerce.orderservice.dto.response.OrderSelectResponse;
import com.company.ecommerce.orderservice.servicer.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CartClient cartClient;
    private final OrderService orderService;


    @GetMapping("/cart-test")
    public ApiResponse<CartResponse> testCart(@RequestHeader("X-User-Id") Long userId) {
        return cartClient.getCartCheckout(userId);
    }


    @PostMapping("/create")
    public ApiResponse<Long> createOrder(@RequestHeader("X-User-Id") Long userId,
                                         @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(userId, request);
    }

    @GetMapping("/orderSelectList")
    public ApiResponse<OrderSelectListResponse> selectList(
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ApiResponse.success("Orders retrieved", orderService.selectListOrder(userId), 200);
    }

    @GetMapping("/acquiredOrder/{id}")
    public ApiResponse<OrderSelectResponse> selectOrderByOrderId(@PathVariable("id") Long orderId,
                                                                 @RequestHeader("X-User-Id") Long userId
    ) {
        return ApiResponse.success("Order retrieved", orderService.selectOrderByOrderId(orderId, userId), 200);
    }

    @PostMapping("/payment")
    public ApiResponse<Long> Payment(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PaymentRequest orderId
    ) {
        return orderService.orderPaymentCompleted(userId, orderId);
    }

}
