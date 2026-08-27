package com.company.ecommerce.orderservice.servicer.impl;

import com.company.ecommerce.orderservice.client.CartClient;
import com.company.ecommerce.orderservice.client.dto.CartItemResponse;
import com.company.ecommerce.orderservice.client.dto.CartResponse;
import com.company.ecommerce.common.dto.ApiResponse;
import com.company.ecommerce.orderservice.dto.projection.ProductQuantitySummary;
import com.company.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.company.ecommerce.orderservice.dto.request.PaymentRequest;
import com.company.ecommerce.orderservice.dto.response.*;
import com.company.ecommerce.orderservice.mapper.OrderItemMapper;
import com.company.ecommerce.orderservice.mapper.OrderMapper;
import com.company.ecommerce.orderservice.model.OrderItems;
import com.company.ecommerce.orderservice.model.Orders;
import com.company.ecommerce.orderservice.servicer.OrderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Data
@Service
public class OrderServiceImpl implements OrderService {

    private final CartClient cartClient;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CircuitBreakerFactory circuitBreakerFactory;


    private <T> ApiResponse<T> breaker(
            String breakerName,
            Supplier<ApiResponse<T>> method,
            String message,
            int code) {

        return circuitBreakerFactory.create(breakerName).run(method, throwable -> ApiResponse.failure(message, code));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Long> createOrder(Long id, CreateOrderRequest request) {

        //取得購物車
        ApiResponse<CartResponse> cart = breaker(
                "cartCheckout",
                () -> cartClient.getCartCheckout(id),
                "購物車服務發生錯誤",
                500);

        if (!cart.isSuccess() || cart.getData() == null) {
            return ApiResponse.failure(cart.getMessage(), 500);
        }

        if (cart.getData().getItems().isEmpty()) {
            throw new IllegalArgumentException("目前購物車是空的");
        }

        UUID order_on = UUID.randomUUID();

        List<CartItemResponse> cartItems = cart.getData().getItems();

        BigDecimal totalPrice = cartItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        Orders order;

       /// 判斷付款方式
        if (Objects.equals(request.getPaymentMethod(), "cod")) {
            order = Orders.builder()
                    .orderNo(order_on.toString())
                    .userId(id)
                    .totoAmount(totalPrice)
                    .receiverName(request.getReceiverName())
                    .receiverPhone(request.getReceiverPhone())
                    .receiverAddress(request.getReceiverAddress())
                    .remark(request.getRemark())
                    .paymentMethod(request.getPaymentMethod())
                    .status("PAID")
                    .build();
            int insertedOrderCount = orderMapper.insert(order);
            if (insertedOrderCount != 1) {
                throw new IllegalStateException("訂單主檔新增失敗");
            }
        } else {
            order = Orders.builder()
                    .orderNo(order_on.toString())
                    .userId(id)
                    .totoAmount(totalPrice)
                    .receiverName(request.getReceiverName())
                    .receiverPhone(request.getReceiverPhone())
                    .receiverAddress(request.getReceiverAddress())
                    .remark(request.getRemark())
                    .paymentMethod(request.getPaymentMethod())
                    .build();
            int insertedOrderCount = orderMapper.insert(order);
            if (insertedOrderCount != 1) {
                throw new IllegalStateException("訂單主檔新增失敗");
            }
        }

        List<OrderItems> orderItems = cartItems.stream()
                .map(item -> {
                    BigDecimal subtotal = item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    return OrderItems.builder()
                            .orderId(order.getId())
                            .productId(item.getProductId())
                            .skuCode(item.getSkuCode())
                            .productName(item.getProductName())
                            .mainImageUrl(item.getMainImageUrl())
                            .unitPrice(item.getUnitPrice())
                            .quantity(item.getQuantity())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        int insertedItemCount = orderItemMapper.batchInsert(orderItems);
        if (insertedItemCount != orderItems.size()) {
            throw new IllegalStateException("訂單明細批量新增失敗");
        }

        log.info("orderId={}, insertedItemCount={}", order.getId(), insertedItemCount);

        return ApiResponse.success("新增成功", order.getId(), 200);
    }


    @Override
    @Transactional(readOnly = true)
    public OrderSelectListResponse selectListOrder(Long userId) {


        List<Orders> orderAllByUserId = orderMapper.findOrderAllByUserId(userId);

        if (Objects.equals(orderAllByUserId, null) || orderAllByUserId.isEmpty()) {
            throw new IllegalArgumentException("該用戶查無訂單");
        }

        List<Long> orderId = orderAllByUserId.stream()
                .map(Orders::getId)
                .toList();

        List<OrderItems> allOrderItemsByOrderIds = orderItemMapper.findAllOrderItemsByOrderIds(orderId);

        Map<Long, List<OrderItems>> itemMap = allOrderItemsByOrderIds.stream()
                .collect(Collectors.groupingBy(OrderItems::getOrderId));

        List<OrderSelectResponse> orderSelectResponses = orderAllByUserId.stream()
                .map(item -> {
                    List<OrderItems> orderItems = itemMap
                            .getOrDefault(item.getId(), List.of());

                    List<OrderItemsResponse> orderItemsResponses = orderItems
                            .stream()
                            .map(e -> new OrderItemsResponse(
                                    e.getId(),
                                    e.getOrderId(),
                                    e.getProductId(),
                                    e.getSkuCode(),
                                    e.getProductName(),
                                    e.getMainImageUrl(),
                                    e.getUnitPrice(),
                                    e.getQuantity(),
                                    e.getSubtotal(),
                                    e.getCreatedAt()
                            )).toList();

                    return new OrderSelectResponse(
                            item.getId(),
                            item.getOrderNo(),
                            item.getUserId(),
                            item.getTotoAmount(),
                            item.getStatus(),
                            item.getPaymentDeadline(),
                            item.getReceiverName(),
                            item.getReceiverPhone(),
                            item.getReceiverAddress(),
                            item.getPaymentMethod(),
                            item.getRemark(),
                            item.getCreatedAt(),
                            orderItemsResponses
                    );

                }).toList();

        log.info("orderSelectResponses={}", orderSelectResponses);

        return new OrderSelectListResponse(orderSelectResponses);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSelectResponse selectOrderByOrderId(Long orderId, Long userId) {

        Orders order = orderMapper.findById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("該用戶查無訂單");
        } else if (!Objects.equals(order.getUserId(), userId)) {
            throw new IllegalArgumentException("只能查詢該用戶訂單");
        }

        List<OrderItems> allOrderItemsByOrderIds = orderItemMapper
                .findAllOrderItemsByOrderIds(List.of(order.getId()));

        List<OrderItemsResponse> orderItemsResponseList =
                allOrderItemsByOrderIds.stream().map(e ->
                        new OrderItemsResponse(
                                e.getId(),
                                e.getOrderId(),
                                e.getProductId(),
                                e.getSkuCode(),
                                e.getProductName(),
                                e.getMainImageUrl(),
                                e.getUnitPrice(),
                                e.getQuantity(),
                                e.getSubtotal(),
                                e.getCreatedAt()
                        )
                ).toList();


        return new OrderSelectResponse(
                order.getId(),
                order.getOrderNo(),
                order.getUserId(),
                order.getTotoAmount(),
                order.getStatus(),
                order.getPaymentDeadline(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getReceiverAddress(),
                order.getPaymentMethod(),
                order.getRemark(),
                order.getCreatedAt(),
                orderItemsResponseList
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Long> orderPaymentCompleted(Long userId, PaymentRequest orderId) {
        Orders byId = orderMapper.findById(orderId.getOrderId());

        if (byId == null) {
            throw new IllegalArgumentException("查無此訂單");
        } else if (!Objects.equals(byId.getUserId(), userId)) {
            throw new IllegalArgumentException("操作錯誤 21102151103");
        }

        orderMapper.updateOrderStatus(orderId.getOrderId(), "PAID");

        return ApiResponse.success("付款成功", orderId.getOrderId(), 200);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderExpiredCheckResponse orderExpiredCheck() {

        List<Long> longs = orderMapper.selectExpiredOrderIds();

        int i = orderMapper.cancelExpiredOrders();

        OrderExpiredCheckResponse orderExpiredCheckResponse = new OrderExpiredCheckResponse();
        orderExpiredCheckResponse.setChangeQuantity(i);
        orderExpiredCheckResponse.setOrderIds(longs);

        return orderExpiredCheckResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int orderShipment() {

        String userHome = System.getProperty("user.home");

        Path path = Path.of(userHome, "Desktop", "order_no.txt");

        try {
            String content = Files.readString(path);
            List<String> orderNos = Arrays.stream(content.trim().split("\\R"))
                    .map((String::trim))
                    .toList();

            return orderMapper.orderStatusShipment(orderNos);
        } catch (Exception e) {
            log.info("錯誤訊息={}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }


    }

    @Transactional(readOnly = true)
    public List<ProductQuantitySummary> replenishProductQuantity(List<Long> orderIds) {
        return orderItemMapper.replenishProductQuantity(orderIds);
    }
}

