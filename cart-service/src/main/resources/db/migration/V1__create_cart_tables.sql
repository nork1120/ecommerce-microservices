CREATE TABLE carts(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '購物車ID',
    user_id BIGINT NOT NULL COMMENT '使用者ID',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '購物車狀態：ACTIVE, CHECKED_OUT',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',

    UNIQUE KEY uk_carts_user_active (user_id, status),
    INDEX idx_carts_user_id (user_id)

)COMMENT '購物車主表';

CREATE TABLE cart_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT  '購物商品ID',
    cart_id BIGINT NOT NULL COMMENT '主表購物車ID',
    product_id BIGINT NOT NULL COMMENT '商品ID 來自product-service',
    sku_code VARCHAR(50) NOT NULL COMMENT '商品 sku 來自product-service',
    product_name VARCHAR(50) NOT NULL COMMENT '商品快照名稱',
    main_image_url VARCHAR(500) COMMENT '商品主圖快照',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '加入購物車當下單價快照',
    quantity INT NOT NULL COMMENT '數量',
    selected TINYINT NOT NULL DEFAULT '1' COMMENT '是否勾選結帳 1否 2是',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',

    UNIQUE KEY uk_cart_product (cart_id, product_id),
    INDEX idx_cart_items_cart_id (cart_id),
    INDEX idx_cart_items_product_id (product_id)
)COMMENT '購物車商品明細表';