package com.company.ecommerce.productservice.mapper.product;

import com.company.ecommerce.productservice.dto.request.CreateCategoryRequest;
import com.company.ecommerce.productservice.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 創建類別
     *
     * @param category
     * @return
     */
    int insert(Category category);

    /**
     * 搜尋對應ID的資料
     *
     * @param id
     * @return
     */
    Category getCategoryById(@Param("id") Long id);

    /**
     * 搜尋所有類別
     *
     * @return
     */
    List<Category> selectAll();

    /**
     * 搜尋類別1的大類
     *
     * @return
     */
    List<Category> selectAllLevelOne();

    /**
     * 搜尋該類別底下所有類別的類別ID
     *
     * @param categoryName 類別名稱
     * @return
     */
    List<Category> selectTreeStructuredCategoriesId(@Param("categoryName") String categoryName);

}
