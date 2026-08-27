package com.company.ecommerce.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;

    ///類別名稱
    private String categoryName;

    ///父類ID 如果是頂類就NULL
    private Long parentId;

    ///層級 1:大類 2:次類 3:次次類...
    private Integer level;

    ///由前端決定排列順序
    private Integer sortOrder;


}
