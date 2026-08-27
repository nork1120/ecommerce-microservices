package com.company.ecommerce.cartservice.controller;


import com.company.ecommerce.cartservice.dto.request.CreateCartItemRequest;
import com.company.ecommerce.cartservice.dto.request.ReviseCartItemRequest;
import com.company.ecommerce.cartservice.dto.request.ReviseCartItemSelectedRequest;
import com.company.ecommerce.cartservice.dto.request.UpdateCartItemRequest;
import com.company.ecommerce.cartservice.dto.response.CartItemResponse;
import com.company.ecommerce.cartservice.dto.response.CartResponse;
import com.company.ecommerce.cartservice.servicer.CartService;
import com.company.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

//    @GetMapping
//    public ApiResponse<Long> findActiveCartUserId(@RequestParam Long userID) {
//
//        Long activeCartUserId = cartService.findActiveCartUserId(userID);
//
//        return ApiResponse.success("成功", activeCartUserId);
//    }

    /**
     * 將商品新增至購物車
     *
     * @param userID
     * @param request
     * @return
     */
    @PostMapping("/item")
    public ApiResponse<CartItemResponse> createCartItem(
            @RequestHeader("X-User-Id") Long userID,
            @Valid @RequestBody CreateCartItemRequest request
    ) {
        return cartService.createCartItem(userID, request);
    }

    /**
     * 新增購物車內商品數量
     *
     * @param userId
     * @param request
     * @return
     */
    @PutMapping("/addCartItem")
    public ApiResponse<String> addCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid ReviseCartItemRequest request
    ) {
        String data = cartService.addCartItem(userId, request);
        return ApiResponse.success("成功", data,200);
    }

    /**
     * 減少購物車內商品數量
     *
     * @param userId
     * @param request
     * @return
     */
    @PutMapping("/decreaseCartItem")
    public ApiResponse<String> decreaseCartItem(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody @Valid ReviseCartItemRequest request
    ) {
        String data = cartService.decreaseCartItem(userId, request);
        return ApiResponse.success("成功", data,200);
    }

    @PutMapping("/reviseSelected")
    public ApiResponse<String> updateCartItemSelected(
            @RequestHeader("X-User-Id") Long userID,
            @Valid @RequestBody ReviseCartItemSelectedRequest request
    ) {

        String data = cartService.updateCartItemSelected(userID, request);

        return ApiResponse.success("成功", data,200);
    }


    @GetMapping
    public ApiResponse<CartResponse> getCart(
            @RequestHeader("X-User-Id") Long userID
    ) {

        CartResponse cart = cartService.getCart(userID);

        return ApiResponse.success("成功", cart,200);
    }

    @GetMapping("/getCartCheckout")
    public ApiResponse<CartResponse> getCartCheckout(
            @RequestHeader("X-User-Id") Long userID
    ) {

        CartResponse cart = cartService.getCartCheckout(userID);

        return ApiResponse.success("成功", cart,200);
    }

    @PutMapping("/updateItem/{productId}")
    public ApiResponse<String> updateItem(
            @RequestHeader("X-User-Id") Long userID,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {

        String data = cartService.updateItem(userID, productId, request);

        return ApiResponse.success("修改成功", data,200);
    }


    @DeleteMapping("/removeItem/{id}")
    public ApiResponse<Void> removeItem(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id") Long userID
    ) {
        cartService.deleteItem(userID, id);
        return ApiResponse.success("Cart item removed", null, 200);
    }

    @DeleteMapping("/clearCart")
    public ApiResponse<Void> clearCart(@RequestHeader("X-User-Id") Long userID) {
        cartService.clearCart(userID);
        return ApiResponse.success("Cart cleared", null, 200);
    }

}
