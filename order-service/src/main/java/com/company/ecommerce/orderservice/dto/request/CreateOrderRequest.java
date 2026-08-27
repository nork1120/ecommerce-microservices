package com.company.ecommerce.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {


    ///收件人姓名
    @NotBlank(message = "收件人姓名必填")
    private String receiverName;

    ///手機號碼
    @NotBlank(message = "手機號碼必填")
    private String receiverPhone;

    ///收件人地址
    @NotBlank(message = "收件人地址必填")
    private String receiverAddress;

    ///付款方式:LinePay(1) 現金支付(2) 信用卡(3)
    @NotBlank(message = "付款方式必填")
    private String paymentMethod;

    ///訂單備註
    private String remark;

}
