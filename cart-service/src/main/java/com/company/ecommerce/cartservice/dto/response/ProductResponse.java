package com.company.ecommerce.cartservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    /**
     * 商品主鍵 ID
     */
    private Long id;

    /**
     * SPU ID，代表同一款商品集合
     * 例如：IPHONE15
     */
    private String spuId;

    /**
     * SKU 編碼，代表單一具體規格
     * 例如：IPHONE15-BLACK-256G
     */
    private String skuCode;

    /**
     * 商品名稱
     */
    private String name;

    /**
     * 商品品牌
     * 例如：Apple、Samsung
     */
    private String brand;

    /**
     * 商品分類 ID
     */
    private Long categoryId;

    /**
     * 商品詳細描述
     */
    private String description;

    /**
     * 商品售價
     */
    private BigDecimal price;

    /**
     * 商品庫存數量
     */
    private Integer stock;

    /**
     * 商品主圖 URL
     */
    private String mainImageUrl;

    /**
     * 商品狀態
     * ACTIVE：上架
     * INACTIVE：下架
     */
    private String status;

}
