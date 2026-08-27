package com.company.ecommerce.productservice.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageProductResponse {

    ///目前頁數
    private int page;

    ///搜尋到的總數量
    private int productCount;

    ///總共有幾頁
    private int totalPage;

    ///商品資料
    private List<ProductResponse>  productList;
}
