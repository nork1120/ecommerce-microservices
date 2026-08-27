package com.company.ecommerce.cartservice.mapper;

import com.company.ecommerce.cartservice.model.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {

    /**
     * 新增一筆購物車明細到 cart_items 表，包含商品、數量、單價與是否選取等資料。
     *
     * @param cartItem
     * @return
     */
    int insertCartItem(CartItem cartItem);

    /**
     * 用 cart_id 和 product_id 查詢同一個購物車內是否已存在該商品。
     *
     * @param productId 商品ID
     * @param cartId    使用者購物車ID
     * @return 回傳CartItem表購物車內的該商品
     */
    CartItem findByCartIdAndProductId(@Param("productId") Long productId, @Param("cartId") Long cartId);

    /**
     * 用購物車明細ID 查詢商品
     * @param cartItemId
     * @return
     */
    CartItem findByCartItemId(@Param("cartItemId") Long cartItemId);

    /**
     * 查詢指定 cart_id 底下的所有購物車明細。
     *
     * @param cartId 用戶購物車ID
     * @return 回傳購物車明細屬於這個用戶的所有商品
     */
    List<CartItem> findByCartId(@Param("cartId") Long cartId);


    /**
     * 查詢指定 cart_id 底下的selected=1購物車明細。
     *
     * @param cartId 用戶購物車ID
     * @return 回傳購物車明細屬於這個用戶的所有商品
     */
    List<CartItem> findByCartIdAndSelected(@Param("cartId") Long cartId);

    /**
     * 依購物車明細 id 直接更新 quantity 欄位為指定數量。
     *
     * @param id       購物車明細ID
     * @param quantity 數量
     * @return
     */
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 修改購物車內商品 選定狀態 Selected
     * @param cartItemId
     * @param selected
     * @return
     */
    int updateCartItemSelected(@Param("cartItemId") Long cartItemId, @Param("selected") Integer selected);

    /**
     * 依購物車明細 id 將 quantity 加上指定數量，通常用於重複加入同商品時累加數量。
     *
     * @param id       購物車明細ID
     * @param quantity 數量
     * @return
     */
    int increaseQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 依購物車明細 id 將 quantity 加上減掉數量。
     * @param id
     * @param quantity
     * @return
     */
    int decreaseQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);


    /**
     * 依購物車明細 id 刪除單一商品項目。
     *
     * @param id 購物車明細ID
     * @return
     */
    int deleteById(@Param("id") Long id);

    /**
     * 依 cart_id 刪除該購物車底下的所有商品項目，用於清空購物車。
     *
     * @param cartId 用戶購物車ID
     * @return
     */
    int deleteByCartId(@Param("cartId") Long cartId);

    /**
     * 依 用戶ID跟商品ID 直接修改購物車明細商品數量
     *
     * @param cartId    購物車ID
     * @param productId 商品ID
     * @param quantity  數量
     * @return
     */
    int updateQuantityByCardIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

}
