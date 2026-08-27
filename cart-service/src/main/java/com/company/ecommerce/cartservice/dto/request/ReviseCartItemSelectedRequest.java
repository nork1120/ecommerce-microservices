package com.company.ecommerce.cartservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviseCartItemSelectedRequest {

    @NotNull
    private Long id;

    @NotNull
    @Min(value = 1, message = "selected 只能是 1 或 2")
    @Max(value = 2, message = "selected 只能是 1 或 2")
    private Integer selected;

}
