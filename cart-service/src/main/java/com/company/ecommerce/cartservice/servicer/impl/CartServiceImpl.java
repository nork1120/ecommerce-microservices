package com.company.ecommerce.cartservice.servicer.impl;

import com.company.ecommerce.cartservice.client.ProductClient;
import com.company.ecommerce.cartservice.dto.request.CreateCartItemRequest;
import com.company.ecommerce.cartservice.dto.request.ReviseCartItemRequest;
import com.company.ecommerce.cartservice.dto.request.ReviseCartItemSelectedRequest;
import com.company.ecommerce.cartservice.dto.request.UpdateCartItemRequest;
import com.company.ecommerce.cartservice.dto.response.CartItemResponse;
import com.company.ecommerce.cartservice.dto.response.CartResponse;
import com.company.ecommerce.cartservice.dto.response.ProductResponse;
import com.company.ecommerce.common.dto.ApiResponse;
import com.company.ecommerce.cartservice.mapper.CartItemMapper;
import com.company.ecommerce.cartservice.mapper.CartMapper;
import com.company.ecommerce.cartservice.model.Cart;
import com.company.ecommerce.cartservice.model.CartItem;
import com.company.ecommerce.cartservice.servicer.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductClient productClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    private <T> ApiResponse<T> breaker(
            String breakerName,
            Supplier<ApiResponse<T>> action,
            String message,
            int code
    ) {
        return circuitBreakerFactory.create(breakerName)
                .run(
                        action,
                        throwable -> {
                            log.error(
                                    "Circuit breaker fallback: breakerName={}",
                                    breakerName,
                                    throwable
                            );
                            return ApiResponse.failure(message, code);
                        }
                );
    }

    private <T> T requireData(
            ApiResponse<T> response,
            String emptyDataMessage
    ) {
        if (response == null) {
            throw new IllegalStateException("下游服務沒有回傳結果");
        }

        if (!response.isSuccess()) {
            throw new IllegalStateException(response.getMessage());
        }

        if (response.getData() == null) {
            throw new IllegalStateException(emptyDataMessage);
        }

        return response.getData();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CartItemResponse> createCartItem(Long userID, CreateCartItemRequest request) {

        ApiResponse<ProductResponse> productResponse = breaker(
                "productService",
                () -> productClient.getProductById(request.getProductId()),
                "商品服務暫時無法使用",
                503
        );

        if (productResponse == null) {
            return ApiResponse.failure("商品服務沒有回傳資料", 502);
        }

        if (!productResponse.isSuccess()) {
            return ApiResponse.failure(
                    productResponse.getMessage(),
                    productResponse.getCode()
            );
        }

        ProductResponse product = productResponse.getData();

        if (product == null) {
            throw new IllegalArgumentException("查無此商品");
        }

        if (!"ACTIVE".equals(product.getStatus())) {
            throw new IllegalArgumentException("商品未上架");
        }


        Cart cart = getOrCreateCart(userID);

        CartItem cartItem = cartItemMapper.findByCartIdAndProductId(request.getProductId(), cart.getId());
        int cartItemQuantity = cartItem == null ? 0 : cartItem.getQuantity();
        int totoQuantity = request.getQuantity() + cartItemQuantity;

        if (cartItem != null) {
            if (product.getStock() < totoQuantity) {
                throw new IllegalArgumentException("數量不足");
            }
            cartItemMapper.increaseQuantity(cartItem.getId(), request.getQuantity());
            return ApiResponse.success(
                    "已經有相同商品改為新增" + request.getQuantity() + "件",
                    null,
                    201
            );
        }

        if (product.getStock() < totoQuantity) {
            throw new IllegalArgumentException("數量不足");
        }


        CartItem item = CartItem.builder()
                .cartId(cart.getId())
                .productId(product.getId())
                .skuCode(product.getSkuCode())
                .productName(product.getName())
                .mainImageUrl(product.getMainImageUrl())
                .unitPrice(product.getPrice())
                .quantity(request.getQuantity())
                .selected(2)
                .build();


        cartItemMapper.insertCartItem(item);

        CartItem selectItem = cartItemMapper.findByCartItemId(item.getId());
        CartItemResponse cartItemResponse = toResponse(selectItem, selectItem.getUnitPrice());


        return ApiResponse.success("新增成功", cartItemResponse, 200);
    }

    /**
     * 將商品數量新增
     *
     * @param userID
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addCartItem(Long userID, ReviseCartItemRequest request) {

        Cart cart = getOrCreateCart(userID);

        CartItem cartIdAndProductId = cartItemMapper.findByCartIdAndProductId(request.getProductId(), cart.getId());
        if (cartIdAndProductId == null) {
            return "購物車內沒有此商品新增失敗";
        }


        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();

        ApiResponse<ProductResponse> productResponse = breaker(
                "productService",
                () -> productClient.getProductById(request.getProductId()),
                "商品服務暫時無法使用",
                503
        );
        ProductResponse productById = requireData(
                productResponse,
                "商品服務沒有回傳商品資料"
        );

        if (productById.getStock() < quantity + cartIdAndProductId.getQuantity()) {
            return "新增數量後超過商品庫存";
        }

        cartItemMapper.increaseQuantity(cartIdAndProductId.getId(), quantity);

        return "新增商品數量成功";
    }

    /**
     * 扣除商品數量 如果商品數量不夠 將移除商品
     *
     * @param userID
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String decreaseCartItem(Long userID, ReviseCartItemRequest request) {

        Cart cart = getOrCreateCart(userID);

        CartItem byCartIdAndProductId = cartItemMapper.findByCartIdAndProductId(request.getProductId(), cart.getId());

        if (byCartIdAndProductId == null) {
            return "購物車內沒有此商品扣除失敗";
        }

        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();

        if (byCartIdAndProductId.getQuantity() <= quantity) {
            cartItemMapper.deleteById(byCartIdAndProductId.getId());
            return "商品數量小於要扣除數量 已將購物車內商品移除";
        }


        cartItemMapper.decreaseQuantity(byCartIdAndProductId.getId(), quantity);

        return "扣除數量成功";
    }

    /**
     * 修改商品選定狀態
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCartItemSelected(Long userId, ReviseCartItemSelectedRequest request) {

        Cart cart = getOrCreateCart(userId);

        CartItem cartItem = cartItemMapper.findByCartItemId(request.getId());

        if (cartItem == null) {
            return "查無此商品";
        } else if (!Objects.equals(cartItem.getCartId(), cart.getId())) {
            return "只能修改自己購物車內的商品";
        }

        cartItemMapper.updateCartItemSelected(request.getId(), request.getSelected());

        return "修改成功";
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartResponse getCart(Long userID) {

        Cart cart = getOrCreateCart(userID);

        List<CartItem> cartItems = cartItemMapper.findByCartId(cart.getId());
        ///判斷購物車是否為空 是空的直接回傳 空的購物車
        if (cartItems.isEmpty()) {
            return CartResponse.builder()
                    .cartId(cart.getId())
                    .userId(cart.getUserId())
                    .items(List.of())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        List<Long> ids = cartItems.stream().map(CartItem::getProductId).toList();

        ApiResponse<List<ProductResponse>> productResponse = breaker(
                "productService",
                () -> productClient.getProductsInIds(ids),
                "商品服務暫時無法使用",
                503
        );
        List<ProductResponse> productsInIds = requireData(
                productResponse,
                "商品服務沒有回傳商品資料"
        );


        Map<Long, ProductResponse> map = productsInIds.stream().collect(Collectors.toMap(
                ProductResponse::getId,
                productResponses -> productResponses
        ));


        List<CartItemResponse> cartItemResponseList = cartItems.stream().map(item -> {
            ProductResponse product = map.get(item.getProductId());
            if (product == null) {
                throw new IllegalStateException(
                        "商品服務缺少商品資料，productId=" + item.getProductId()
                );
            }

            BigDecimal price = product.getPrice();
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));


            return CartItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .skuCode(item.getSkuCode())
                    .productName(item.getProductName())
                    .mainImageUrl(item.getMainImageUrl())
                    .unitPrice(item.getUnitPrice())
                    .productPrice(price)
                    .quantity(item.getQuantity())
                    .subTotal(subTotal)
                    .selected(item.getSelected())
                    .build();
        }).toList();

        BigDecimal totalAmount = cartItemResponseList.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("cartItems={}", cartItems);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(cartItemResponseList)
                .totalAmount(totalAmount)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartResponse getCartCheckout(Long userID) {

        Cart cart = getOrCreateCart(userID);

        List<CartItem> itemList = cartItemMapper.findByCartIdAndSelected(cart.getId());

        if (itemList.isEmpty()) {
            return CartResponse.builder()
                    .cartId(cart.getId())
                    .userId(cart.getUserId())
                    .items(List.of())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        List<Long> list = itemList.stream().map(CartItem::getProductId).toList();

        ApiResponse<List<ProductResponse>> productResponse = breaker(
                "productService",
                () -> productClient.getProductsInIds(list),
                "商品服務暫時無法使用",
                503
        );
        List<ProductResponse> productsInIds = requireData(
                productResponse,
                "商品服務沒有回傳結帳商品資料"
        );

        Map<Long, ProductResponse> collect = productsInIds.stream().collect(Collectors.toMap(
                ProductResponse::getId,
                product -> product
        ));

        List<CartItemResponse> itemListResponse = itemList.stream().map(item -> {

                    ProductResponse product = collect.get(item.getProductId());
                    if (product == null) {
                        throw new IllegalStateException(
                                "商品服務缺少結帳商品資料，productId=" + item.getProductId()
                        );
                    }

                    BigDecimal price = product.getPrice();

                    return CartItemResponse.builder()
                            .id(item.getId())
                            .productId(item.getProductId())
                            .skuCode(item.getSkuCode())
                            .productName(item.getProductName())
                            .mainImageUrl(item.getMainImageUrl())
                            .unitPrice(item.getUnitPrice())
                            .productPrice(price)
                            .quantity(item.getQuantity())
                            .subTotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                            .selected(item.getSelected())
                            .build();
                }
        ).toList();
        BigDecimal totalAmount = itemListResponse.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(itemListResponse)
                .totalAmount(totalAmount)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateItem(Long userID, Long productId, UpdateCartItemRequest updateCartItemRequest) {

        Cart activeCartUserId = getOrCreateCart(userID);
//        if (activeCartUserId == null) {
//            throw new IllegalArgumentException("查無用戶購物車ID");
//        }
        int i = cartItemMapper.updateQuantityByCardIdAndProductId(
                activeCartUserId.getId()
                , productId
                , updateCartItemRequest.getQuantity());


        return "成功修改" + i;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long userID, Long productID) {

        Cart activeCartUserId = getOrCreateCart(userID);
//        if (activeCartUserId == null) {
//            throw new IllegalArgumentException("查無用戶購物車ID");
//        }
        CartItem byCartIdAndProductId = cartItemMapper.findByCartIdAndProductId(
                productID,
                activeCartUserId.getId()
        );
        if (byCartIdAndProductId == null) {
            throw new IllegalArgumentException("查無此商品明細");
        }
        cartItemMapper.deleteById(byCartIdAndProductId.getId());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userID) {
        Cart activeCartUserId = getOrCreateCart(userID);
//        if (activeCartUserId == null) {
//            throw new IllegalArgumentException("查無用戶購物車ID");
//        }

        cartItemMapper.deleteByCartId(activeCartUserId.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long findActiveCartUserId(Long userID) {

        Cart userCartId = getOrCreateCart(userID);

//        if (userCartId == null) {
//            throw new IllegalArgumentException("查無此使用者");
//        }

        return userCartId.getId();
    }

    private Cart getOrCreateCart(Long userID) {
        Cart activeCart = cartMapper.findActiveCartUserId(userID);

        if (activeCart != null) {
            return activeCart;
        }

        Cart newCart = new Cart();

        newCart.setUserId(userID);
        newCart.setStatus("ACTIVE");

        cartMapper.insert(newCart);

        return newCart;
    }

    private CartItemResponse toResponse(CartItem cartItem, BigDecimal productPrice) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .skuCode(cartItem.getSkuCode())
                .productName(cartItem.getProductName())
                .mainImageUrl(cartItem.getMainImageUrl())
                .unitPrice(cartItem.getUnitPrice())
                .productPrice(cartItem.getUnitPrice())
                .productPrice(productPrice)
                .quantity(cartItem.getQuantity())
                .subTotal(productPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .selected(cartItem.getSelected())
                .build();
    }

}
