CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主鍵 ID，系統自動遞增',
                          spu_id VARCHAR(50) NOT NULL COMMENT 'SPU ID，代表同一款商品集合（如：iPhone 15）',
                          sku_code VARCHAR(50) UNIQUE NOT NULL COMMENT 'SKU 編碼，代表單一具體規格（如：iPhone 15 黑色 256G），全系統唯一',
                          name VARCHAR(100) NOT NULL COMMENT '商品名稱，顯示給使用者的名稱',
                          brand VARCHAR(50) COMMENT '商品品牌，如：Apple, Sony',
                          category_id BIGINT COMMENT '分類 ID，對應分類表 (Category) 的主鍵',
                          description TEXT COMMENT '商品詳細描述與圖文資訊',
                          price DECIMAL(10, 2) NOT NULL CHECK ( price >=0 ) COMMENT '商品定價，精確至小數點後兩位',
                          stock INT NOT NULL DEFAULT 0 CHECK ( stock>=0 ) COMMENT '庫存數量，當前可售餘額',
                          main_image_url VARCHAR(500) COMMENT '商品列表頁顯示的主圖 URL',
                          status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '商品狀態：ACTIVE(上架), INACTIVE(下架), DRAFT(草稿)',
                          is_deleted TINYINT DEFAULT 0 COMMENT '邏輯刪除標記：0 為存在，1 為已刪除',
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '資料建立時間',
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '資料最後更新時間',

                          INDEX idx_products_spu_id (spu_id),
                          INDEX idx_products_category_id (category_id),
                          INDEX idx_products_status (status),
                          INDEX idx_products_is_deleted (is_deleted)
) COMMENT '商品資訊表';

CREATE TABLE category(
                    id BIGINT AUTO_INCREMENT PRIMARY KEY  COMMENT '商品類別ID',
                    category_name VARCHAR(20) NOT NULL COMMENT '類別名稱',
                    parent_id BIGINT DEFAULT NULL COMMENT '父類ID 如果是頂類就NULL',
                    level INT NOT NULL DEFAULT 1 COMMENT '層級 1:大類 2:次類 3:次次類...',
                    sort_order INT NOT NULL DEFAULT 0 COMMENT '由前端決定排列順序',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '資料建立時間',

                    FOREIGN KEY (parent_id) REFERENCES category(id)
) COMMENT '商品類別';