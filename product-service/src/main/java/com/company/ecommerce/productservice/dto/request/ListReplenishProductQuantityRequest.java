package com.company.ecommerce.productservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ListReplenishProductQuantityRequest {

    @NotEmpty
    @Valid
    private List<ReplenishProductQuantityRequest> replenishProductQuantityRequestList;

}
