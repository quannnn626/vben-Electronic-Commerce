package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCallbackRequest {

    /**
     * 商户支付单号
     */
    private String paymentNo;

    /**
     * 第三方交易号
     */
    private String tradeNo;

    /**
     * 支付状态
     */
    private String tradeStatus;

    /**
     * 实际支付金额
     */
    private BigDecimal amount;
}
