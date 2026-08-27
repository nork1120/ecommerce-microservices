package com.company.ecommerce.orderservice.mapper;

import com.company.ecommerce.orderservice.model.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insert(Orders order);

    Orders findById(@Param("id") Long id);

    Orders findByOrderNo(@Param("orderNo") String orderNo);

    List<Orders> findOrderAllByUserId(@Param("userId") Long userId);

    /**
     * 傳入訂單ID 與 status 修改訂單狀態
     * @param id
     * @param status
     * @return
     */
    int updateOrderStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 取消付款時間過期的訂單
     * @return
     */
    int cancelExpiredOrders();

    /**
     * 搜尋出過期的訂單
     * @return
     */
    List<Long> selectExpiredOrderIds();

    /**
     * 將訂單改成出貨
     * @param orderIds
     * @return
     */
    int orderStatusShipment(@Param("ids") List<String> orderIds);



}
