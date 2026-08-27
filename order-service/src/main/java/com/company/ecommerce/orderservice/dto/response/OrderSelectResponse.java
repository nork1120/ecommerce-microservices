package com.company.ecommerce.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderSelectResponse {

    ///訂單ID
    private Long id;

    ///訂單編號 系統唯一
    private String orderNo;

    ///用戶ID
    private Long userId;

    ///訂單總金額
    private BigDecimal totoAmount;

    ///訂單狀態：CREATED, PAID, CANCELLED, SHIPPED, COMPLETED
    private String status;

    ///付款截止時間
    private LocalDateTime paymentDeadline;

    ///收件人姓名
    private String receiverName;

    ///收件人手機號碼
    private String receiverPhone;

    ///收件人地址
    private String receiverAddress;

    ///付款方式
    private String paymentMethod;

    ///訂單備註
    private String remark;

    ///建立時間
    private LocalDateTime createdAt;

    ///商品明細
    private List<OrderItemsResponse> items;

}
