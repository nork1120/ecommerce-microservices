package com.company.ecommerce.productservice.mapper.product;

import com.company.ecommerce.productservice.dto.request.DeductionStock;
import com.company.ecommerce.productservice.dto.projection.ProductStockAdjustment;
import com.company.ecommerce.productservice.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ProductMapper {

    /**
     * 創建商品
     *
     * @param product
     * @return
     */
    int insert(Product product);

    /**
     * 用商品ID查詢商品
     *
     * @param id
     * @return
     */
    Product findById(@Param("id") Long id);

    /**
     * 用商品ID查詢商品 IN ()
     * @param ids
     * @return
     */
    List<Product> findInIds(@Param("ids") List<Long> ids);

    /**
     *用skuCode查詢商品
     * @param skuCode
     * @return
     */
    Product findBySkuCode(@Param("skuCode") String skuCode);

    /**
     * 用supId 查詢 SpuId一樣的群組商品
     * @param spuId
     * @return
     */
    List<Product> findBySpuIdProductList(@Param("spuId") String spuId);

    /**
     * 查詢所有商品 如果categoryIds 不等於 null 或 size !=0  就查詢類別底下的所有商品
     * @param categoryIds 類別Ids
     * @param page 查詢第幾筆
     * @return
     */
    List<Product> findAll(@Param("categoryIds") List<Long> categoryIds, @Param("page") int page);

    /**
     * 查詢單一類別的商品
     *
     * @param categoryId
     * @return
     */
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 用ID找尋商品 把商品軟刪除
     *
     * @param id
     * @return
     */
    int softDeleteById(@Param("id") Long id);

    /**
     * 修改商品資訊 需傳
     * name = #{name},
     * brand = #{brand},
     * category_id = #{categoryId},
     * description = #{description},
     * price = #{price},
     * stock = #{stock},
     * main_image_url = #{mainImageUrl},
     * status = #{status}
     *
     * @param product
     * @return
     */
    int update(Product product);

    /**
     * 結帳用 扣除商品數量
     *
     * @param deductionStock
     * @return
     */
    int findByIdDeductTheStock(@Param("items") List<DeductionStock> deductionStock);

    /**
     * 搜尋商品數量 分頁用
     * @param categoryIds
     * @return
     */
    int selectProductCount(@Param("categoryIds") List<Long> categoryIds);

    /**
     * 補回商品數量用
     * @param items
     * @return
     */
    int replenishProductStock(@Param("items") List<ProductStockAdjustment> items);
}
