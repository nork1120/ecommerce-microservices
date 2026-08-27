package com.company.ecommerce.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    ///訂單ID
    @NotNull
    private Long orderId;
}
