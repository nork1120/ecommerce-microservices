CREATE TABLE orders(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '訂單ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '訂單編號 系統唯一',
    user_id BIGINT NOT NULL COMMENT '用戶ID',

    toto_amount DECIMAL(10,2) NOT NULL COMMENT '訂單總金額',
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED' COMMENT '訂單狀態：CREATED, PAID, CANCELLED, SHIPPED, COMPLETED',

    receiver_name VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    receiver_phone VARCHAR(30) NOT NULL COMMENT '收件人手機號碼',
    receiver_address VARCHAR(255) NOT NULL COMMENT '收件人地址',

    payment_method VARCHAR(50) NOT NULL COMMENT '付款方式:LinePay(1) 現金支付(2) 信用卡(3)',
    remark VARCHAR(500) COMMENT '訂單備註',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',


    INDEX idx_orders_user_id(user_id),
    INDEX idx_orders_status(status)

)COMMENT '訂單主表';

CREATE TABLE order_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明細ID',
    order_id BIGINT NOT NULL COMMENT '主表訂單ID',

    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_code VARCHAR(50) NOT NULL COMMENT '商品 SKU 快照',
    product_name VARCHAR(50) NOT NULL COMMENT '商品名稱快招',
    main_image_url VARCHAR(500) COMMENT '商品url快照',

    unit_price DECIMAL(10,2) NOT NULL COMMENT '下單時商品價個快照',
    quantity INT NOT NULL COMMENT '數量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '總價格',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '建立時間',

    INDEX idx_order_items_order_id (order_id),
    INDEX idx_order_items_product_id (product_id)

)COMMENT '訂單明細表';