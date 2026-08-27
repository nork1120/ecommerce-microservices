package com.company.ecommerce.productservice.servicer;


import com.company.ecommerce.productservice.dto.request.*;
import com.company.ecommerce.productservice.dto.response.CategoryResponse;
import com.company.ecommerce.productservice.dto.response.PageProductResponse;
import com.company.ecommerce.productservice.dto.response.ProductDetailsResponse;
import com.company.ecommerce.productservice.dto.response.ProductResponse;
import com.company.ecommerce.productservice.model.Category;

import java.util.List;

public interface ProductService {
    /**
     * 創建商品
     * @param createProductRequest
     * @return
     */
    ProductResponse createProduct(CreateProductRequest createProductRequest);

    /**
     * 用商品ID 搜尋商品詳細資料 會把群組也加進去
     * @param id
     * @return
     */
    ProductDetailsResponse getById(Long id);

    /**
     * 用商品ID搜尋商品資料 沒有群組單一資料
     * @param id
     * @return
     */
     ProductResponse getByIdSingleDataItem(Long id);

    /**
     * 用商品ID 搜尋商品 用 IN ()
     * @param ids
     * @return
     */
    List<ProductResponse> getProductsInIds(List<Long> ids);

    /**
     * 搜尋分類商品底下的所有商品
     * @param categoryName
     * @param page
     * @return
     */
    PageProductResponse getProductList(String categoryName, int page);

    /**
     * 修改商品內容
     * @param id
     * @param updateProductRequest
     * @return
     */
    ProductResponse updateProduct(Long id, UpdateProductRequest updateProductRequest);

    /**
     * 軟刪除商品
     * @param id
     * @return
     */
    int deleteById(Long id);

    /**
     * 結帳時 扣除商品數量
     * @param deductionStock
     * @return
     */
    int updateQuantity(List<DeductionStock> deductionStock);

    /**
     * 創建類別
     * @param category
     */
    void createCategory(CreateCategoryRequest category);

    /**
     * 搜尋所有類別
     * @return
     */
    List<CategoryResponse> getCategoryAllList();

    /**
     * 將商品數量補回
     * @param request
     * @return
     */
    int replenishProductStock(ListReplenishProductQuantityRequest request);
}
