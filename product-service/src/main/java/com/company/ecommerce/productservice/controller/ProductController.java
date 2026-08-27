package com.company.ecommerce.productservice.controller;

import com.company.ecommerce.productservice.dto.request.*;
import com.company.ecommerce.productservice.dto.response.CategoryResponse;
import com.company.ecommerce.productservice.dto.response.PageProductResponse;
import com.company.ecommerce.productservice.dto.response.ProductDetailsResponse;
import com.company.ecommerce.productservice.dto.response.ProductResponse;
import com.company.ecommerce.productservice.model.Product;
import com.company.ecommerce.productservice.servicer.ProductService;
import com.company.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Update;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 創建商品
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ApiResponse.success("Product created", productService.createProduct(request), 201);
    }

    /**
     * 用商品ID 搜尋商品詳細資料 會把群組也回傳
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailsResponse> getProductById(@PathVariable Long id) {
        return ApiResponse.success("Product retrieved", productService.getById(id), 200);
    }

    @GetMapping("/getProductInformation/{id}")
    public ApiResponse<ProductResponse> getByIdSingleDataItem(@PathVariable Long id) {
        return ApiResponse.success("Product retrieved", productService.getByIdSingleDataItem(id), 200);
    }

    /**
     * 用ID搜尋商品 用 IN ()
     *
     * @param ids
     * @return
     */
    @GetMapping("/getProductInIds")
    public ApiResponse<List<ProductResponse>> getProductsInIds(@RequestParam List<Long> ids) {
        return ApiResponse.success("Products retrieved", productService.getProductsInIds(ids), 200);
    }

    /**
     * 搜尋所有商品 或 類別底下的商品 一次搜尋10筆 有分頁
     *
     * @param category
     * @param page
     * @return
     */
    @GetMapping("/getAll")
    public ApiResponse<PageProductResponse> getAllProducts(@RequestParam(required = false) String category, @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.success("Products retrieved", productService.getProductList(category, page), 200);
    }

    /**
     * 修改商品資訊
     *
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProductById(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ApiResponse.success("Product updated", productService.updateProduct(id, request), 200);
    }

    /**
     * 軟刪除商品
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Integer> deleteProductById(@PathVariable Long id) {
        return ApiResponse.success("Product deleted", productService.deleteById(id), 200);
    }

    /**
     * 結帳時 扣除商品數量
     *
     * @param deductionStock
     * @return
     */
    @PutMapping("/updateStock")
    public ApiResponse<Integer> updateStock(@RequestBody List<DeductionStock> deductionStock) {
        return ApiResponse.success("Product stock updated", productService.updateQuantity(deductionStock), 200);
    }

    /**
     * 新增分類
     *
     * @param category
     * @return
     */
    @PostMapping("/createCategory")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Integer> createCategory(@RequestBody @Valid CreateCategoryRequest category) {
        productService.createCategory(category);
        return ApiResponse.success("Category created", 1, 201);
    }

    /**
     * 取得所有分類
     *
     * @return
     */
    @GetMapping("/getAllCategories")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.success("Categories retrieved", productService.getCategoryAllList(), 200);
    }

    /**
     * 補回商品數量用 IN(id)
     * @param request
     * @return
     */
    @PutMapping("/replenishProduct")
    public ApiResponse<Integer> replenishProductStock(@Valid @RequestBody ListReplenishProductQuantityRequest request) {
        return ApiResponse.success("Product stock replenished", productService.replenishProductStock(request), 200);
    }
}
