package com.company.ecommerce.orderservice.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderItems {

    ///明細ID
    private Long id;

    ///主表訂單ID
    private  Long orderId;

    ///商品ID
    private Long productId;

    ///商品 SKU 快照
    private String skuCode;

    ///商品名稱快招
    private String productName;

    ///商品url快照
    private String mainImageUrl;

    ///下單時商品價個快照
    private BigDecimal unitPrice;

    ///數量
    private Integer quantity;

    ///總價格
    private BigDecimal subtotal;

    ///建立時間
    private LocalDateTime createdAt;

}
