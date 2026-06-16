package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 创建支付请求入参
 */
@Data
public class PaymentCreateRequest {
    private Long orderId;    // 订单ID
    private String payType;  // 支付方式
}
