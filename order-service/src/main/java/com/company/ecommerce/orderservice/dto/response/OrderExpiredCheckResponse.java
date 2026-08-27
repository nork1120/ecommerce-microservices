package com.company.ecommerce.orderservice.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class OrderExpiredCheckResponse {

    ///修改的數量
    private int changeQuantity;
    ///過期的訂單 ID
    private List<Long>  orderIds;
}
