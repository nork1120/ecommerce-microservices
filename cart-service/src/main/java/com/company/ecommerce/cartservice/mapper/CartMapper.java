package com.company.ecommerce.cartservice.mapper;


import com.company.ecommerce.cartservice.model.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CartMapper {

    /**
     *  查詢指定使用者的購物車資料，SQL 會依 user_id 到 carts 表找出對應 cart。
     * @param userID 使用者ID
     * @return 回傳Cart
     */
    Cart findActiveCartUserId(@Param("userID") Long userID);

    /**
     * 新增一筆購物車資料到 carts 表，並把資料庫產生的主鍵 id 回填到 Cart 物件。
     * @param cart
     * @return 會將ID 寫進該物件裡
     */
    int insert(Cart cart);

    /**
     * 依 cart id 更新 carts 表的 status，例如把購物車狀態改成 ACTIVE 或 CHECKED_OUT。
     * @param id 購物車ID
     * @param status 狀態
     * @return 回傳更改幾筆
     */
    int updateStatus(@Param("id") Long id ,@Param("status")  String status);

}
