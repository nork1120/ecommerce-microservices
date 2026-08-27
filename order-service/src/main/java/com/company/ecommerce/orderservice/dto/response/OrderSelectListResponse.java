package com.company.ecommerce.orderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderSelectListResponse {

    private List<OrderSelectResponse> orders;

}
