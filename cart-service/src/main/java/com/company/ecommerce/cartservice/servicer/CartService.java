package com.company.ecommerce.cartservice.servicer;

import com.company.ecommerce.cartservice.dto.request.CreateCartItemRequest;
import com.company.ecommerce.cartservice.dto.request.ReviseCartItemRequest;
import com.company.ecommerce.cartservice.dto.request.ReviseCartItemSelectedRequest;
import com.company.ecommerce.cartservice.dto.request.UpdateCartItemRequest;
import com.company.ecommerce.cartservice.dto.response.CartItemResponse;
import com.company.ecommerce.common.dto.ApiResponse;
import com.company.ecommerce.cartservice.dto.response.CartResponse;

public interface CartService {
    /**
     * 查找用戶購物車ID
     * @param userID
     * @return
     */
    Long findActiveCartUserId(Long userID);

    /**
     * 新增用戶購物車明細內商品數量
     * @param userID 用戶ID
     * @param createCartItemRequest 商品ID 跟 數量
     * @return 回傳是否成功
     */
    ApiResponse<CartItemResponse> createCartItem(Long userID, CreateCartItemRequest createCartItemRequest);

    /**
     * 新增購物車商品數量
     * @param userID
     * @param request
     * @return
     */
    String addCartItem(Long userID, ReviseCartItemRequest request);

    /**
     * 減少購物車商品數量
     * @param userID
     * @param request
     * @return
     */
    String decreaseCartItem(Long userID, ReviseCartItemRequest request);

    /**
     * 修改商品選定狀態
     * @param request
     * @return
     */
    String updateCartItemSelected(Long userId, ReviseCartItemSelectedRequest request);

    /**
     * 取的用戶購物車內所有商品明細
     * @param userID
     * @return
     */
    CartResponse getCart(Long userID);

    /**
     * 結帳用查詢購物車被選種商品 selected = 1的商品
     * @param userID
     * @return
     */
    CartResponse getCartCheckout(Long userID);

    /**
     *修該購物車明細商品數量
     * @param userId 用戶ID
     * @param productId 商品ID
     * @param updateCartItemRequest 數量
     * @return 回傳是否修改成功
     */
    String updateItem(Long userId,Long productId, UpdateCartItemRequest updateCartItemRequest);

    /**
     * 刪除購物車內商品明細
     * @param userID 用戶ID
     * @param productID 商品ID
     */
    void deleteItem(Long userID,Long productID);

    /**
     * 刪除購物車內已被選定的商品明細(結帳用:結帳後刪除購物車內已選定的商品)
     * @param userID
     */
    void deleteCartSelectedItem(Long userID);

    /**
     * 清空用戶購物車內所有商品
     * @param userID
     */
    void clearCart(Long userID);



}
