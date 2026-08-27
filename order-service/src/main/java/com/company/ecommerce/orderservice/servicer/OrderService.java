package com.company.ecommerce.orderservice.servicer;


import com.company.ecommerce.common.dto.ApiResponse;
import com.company.ecommerce.orderservice.dto.request.CreateOrderRequest;
import com.company.ecommerce.orderservice.dto.request.PaymentRequest;
import com.company.ecommerce.orderservice.dto.projection.ProductQuantitySummary;
import com.company.ecommerce.orderservice.dto.response.OrderExpiredCheckResponse;
import com.company.ecommerce.orderservice.dto.response.OrderSelectListResponse;
import com.company.ecommerce.orderservice.dto.response.OrderSelectResponse;

import java.util.List;


public interface OrderService {

    /**
     * 創建訂單
     * @param id
     * @param request
     * @return
     */
    ApiResponse<Long> createOrder(Long id, CreateOrderRequest request);

    /**
     * 搜尋所有訂單
     * @param userId
     * @return
     */
    OrderSelectListResponse selectListOrder(Long userId);

    /**
     * 搜尋單一訂單詳細內容
     * @param orderId
     * @param userId
     * @return
     */
    OrderSelectResponse selectOrderByOrderId(Long orderId,Long  userId);

    /**
     * 訂單付款
     * @param userId
     * @param orderId
     * @return
     */
    ApiResponse<Long> orderPaymentCompleted(Long  userId, PaymentRequest orderId);

    /**
     * 檢查是否有訂單付款時間過期 取消過期訂單
     * @return
     */
    OrderExpiredCheckResponse orderExpiredCheck();

    /**
     * 將桌面的訂單出貨記事本裡面的order_on改成 出貨
     * @return
     */
    int orderShipment();

    /**
     * 找出把需要回補數量的 跟數量
     * @param
     * @return
     */
    List<ProductQuantitySummary> replenishProductQuantity(List<Long> orderIds);

}
