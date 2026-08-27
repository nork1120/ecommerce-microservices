package com.company.ecommerce.orderservice.mapper;

import com.company.ecommerce.orderservice.dto.projection.ProductQuantitySummary;
import com.company.ecommerce.orderservice.model.OrderItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    int batchInsert(@Param("items") List<OrderItems> orderItems);



    /**
     * 查詢一筆訂單內所有的訂單明細
     *
     * @param orderId 訂單編號
     * @return
     */
    List<OrderItems> findAllOrderItemsByOrderId(@Param("orderId") Long orderId);

    /**
     * 查詢指定的多筆訂單所有的訂單明細
     * @param orderIds
     * @return
     */
    List<OrderItems> findAllOrderItemsByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 搜尋出要補充數量的商品ID 跟數量
     * @param orderIds
     * @return
     */
    List<ProductQuantitySummary> replenishProductQuantity(@Param("orderIds") List<Long> orderIds);

}
