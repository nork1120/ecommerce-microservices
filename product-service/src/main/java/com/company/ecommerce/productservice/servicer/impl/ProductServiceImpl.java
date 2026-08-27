package com.company.ecommerce.productservice.servicer.impl;

import com.company.ecommerce.productservice.converter.ProductConverter;
import com.company.ecommerce.productservice.dto.projection.ProductStockAdjustment;
import com.company.ecommerce.productservice.dto.request.*;
import com.company.ecommerce.productservice.dto.response.CategoryResponse;
import com.company.ecommerce.productservice.dto.response.PageProductResponse;
import com.company.ecommerce.productservice.dto.response.ProductDetailsResponse;
import com.company.ecommerce.productservice.dto.response.ProductResponse;
import com.company.ecommerce.productservice.mapper.product.CategoryMapper;
import com.company.ecommerce.productservice.mapper.product.ProductMapper;
import com.company.ecommerce.productservice.model.Category;
import com.company.ecommerce.productservice.model.Product;
import com.company.ecommerce.productservice.servicer.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {


    private final ProductMapper productMapper;
    private final ProductConverter productConverter;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse createProduct(CreateProductRequest createProductRequest) {
        Product existingProduct = productMapper.findBySkuCode(createProductRequest.getSkuCode());

        if (existingProduct != null) {
            throw new RuntimeException("SkuCode 已經重複");
        }

        Product product = Product.builder()
                .skuCode(createProductRequest.getSkuCode()) ///規格
                .spuId(createProductRequest.getSupId()) ///同意款商品的集合
                .name(createProductRequest.getName()) ///商品名稱
                .brand(StringUtils.hasText(createProductRequest.getBrand()) ? createProductRequest.getBrand() : "其他") ///品牌
                .description(createProductRequest.getDescription()) ///商品描述
                .price(createProductRequest.getPrice()) ///價格
                .mainImageUrl(createProductRequest.getMainImageUrl()) ///圖片路徑
                .categoryId(createProductRequest.getCategoryId()) ///分類ID
                .stock(createProductRequest.getStock()) ///數量
                .isDeleted(0)///是否已刪除
                .status(StringUtils.hasText(createProductRequest.getStatus()) ? createProductRequest.getStatus() : "ACTIVE")///狀態
                .build();

        productMapper.insert(product);
        log.info(product.toString());

        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailsResponse getById(Long id) {

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new RuntimeException("無此ID");
        } else if (product.getIsDeleted() != 0) {
            throw new RuntimeException("商品已刪除");
        }

        List<Product> products = productMapper.findBySpuIdProductList(product.getSpuId());

        List<ProductResponse> list = products.stream().map(this::toResponse).toList();


        return new ProductDetailsResponse(
                product.getSpuId(),
                list
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getByIdSingleDataItem(Long id) {

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new RuntimeException("無此ID");
        } else if (product.getIsDeleted() != 0) {
            throw new RuntimeException("商品已刪除");
        }

        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageProductResponse getProductList(String categoryName, int page) {

        List<Long> categoryIds = null;
        PageProductResponse pageProductResponse = new PageProductResponse();
        pageProductResponse.setPage(page);


        ///判斷前端傳來的頁數是不是 1 是的話從0開始找10筆 不是的話找第 page * 10 筆
        page = page <= 1 ? 0 : (page - 1) * 9;

        ///判斷有沒有要找類別
        if (StringUtils.hasText(categoryName)) {
            categoryIds = categoryMapper
                    .selectTreeStructuredCategoriesId(categoryName)
                    .stream()
                    .map(Category::getId)
                    .toList();
        }
        ///搜尋商品
        List<ProductResponse> productResponses = productMapper
                .findAll(categoryIds, page)
                .stream()
                .map(this::toResponse)
                .toList();

        ///搜尋該類商品數量
        int count = productMapper.selectProductCount(categoryIds);

        ///寫入分頁資訊
        pageProductResponse.setProductCount(count);
        pageProductResponse.setTotalPage(Math.ceilDiv(count, 9));
        pageProductResponse.setProductList(productResponses);

        ///查詢所有商品 轉成回傳格式
        return pageProductResponse;
    }

    /// 搜尋商品 用 IN ()
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsInIds(List<Long> ids) {

        List<Product> products = productMapper.findInIds(ids);


        return products.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {

        Product product = productMapper.findById(id);

        if (product == null) {
            throw new RuntimeException("找無此商品");
        }

        productConverter.updateProduct(request, product);
        log.info(product.toString());
        productMapper.update(product);
        return toResponse((product));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(Long id) {

        Product product = productMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("查無此商品");
        }

        return productMapper.softDeleteById(product.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuantity(List<DeductionStock> deductionStock) {
        return productMapper.findByIdDeductTheStock(deductionStock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(CreateCategoryRequest createCategoryRequest) {

        int level;

        if (createCategoryRequest.getParentId() == null) {
            level = 1;
        } else {
            Category categoryById = categoryMapper.getCategoryById(createCategoryRequest.getParentId());
            level = categoryById.getLevel() + 1;
        }


        Category category = new Category();

        if (createCategoryRequest.getSortOrder() == null) {
            category.setSortOrder(0);
        } else {
            category.setSortOrder(createCategoryRequest.getSortOrder());
        }
        category.setCategoryName(createCategoryRequest.getCategoryName());
        category.setParentId(createCategoryRequest.getParentId());
        category.setLevel(level);
        categoryMapper.insert(category);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryAllList() {

        List<Category> categories = categoryMapper.selectAll();

        return categories.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int replenishProductStock(ListReplenishProductQuantityRequest request) {
        List<ProductStockAdjustment> items = request.getReplenishProductQuantityRequestList()
                .stream()
                .map(item -> new ProductStockAdjustment(item.getProductId(), item.getQuantity()))
                .toList();

        return productMapper.replenishProductStock(items);
    }


    private ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .spuId(product.getSpuId())
                .skuCode(product.getSkuCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .mainImageUrl(product.getMainImageUrl())
                .categoryId(product.getCategoryId())
                .stock(product.getStock())
                .brand(product.getBrand())
                .status(product.getStatus())
                .build();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCategoryName(),
                category.getParentId(),
                category.getLevel(),
                category.getSortOrder()
        );
    }


}
