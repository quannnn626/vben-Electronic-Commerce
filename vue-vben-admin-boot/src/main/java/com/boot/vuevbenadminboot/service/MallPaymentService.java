package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallPayment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.req.PaymentCallbackRequest;
import com.boot.vuevbenadminboot.web.dto.req.PaymentCreateRequest;

/**
 * @author quannnn
 * @description 针对表【mall_payment】的数据库操作Service
 * @createDate 2026-06-12 14:30:25
 */
public interface MallPaymentService extends IService<MallPayment> {
    MallPayment createPayment(String username, PaymentCreateRequest paymentCreateRequest);

    MallPayment paymentCallback(PaymentCallbackRequest req);

    MallPayment paymentCallback(String username, PaymentCallbackRequest req);
}
