package com.company.ecommerce.productservice.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategoryRequest {

    ///類別名稱
    @NotNull(message = "名稱不得為空")
    private String categoryName;

    ///父類ID 如果是頂類就NULL
    @Nullable
    @Positive(message = "父類ID 必須大於0")
    private Long parentId;

    ///由前端決定排列順序
    private Integer sortOrder;


}
