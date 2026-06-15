package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

@Data
public class PaymentCreateRequest {
    private Long orderId;    // 订单ID
    private String payType;  // 支付方式
}
