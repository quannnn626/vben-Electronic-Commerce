package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.boot.vuevbenadminboot.domain.MallRefund;
import com.boot.vuevbenadminboot.domain.enums.RefundStatusEnum;
import com.boot.vuevbenadminboot.service.MallRefundService;
import com.boot.vuevbenadminboot.mapper.MallRefundMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MallRefundServiceImpl extends ServiceImpl<MallRefundMapper, MallRefund>
    implements MallRefundService{

    @Override
    public MallRefund createRefund(MallAfterSale afterSale) {
        MallRefund refund = new MallRefund();
        refund.setAfterSaleId(afterSale.getId());
        refund.setOrderId(afterSale.getOrderId());
        refund.setUserId(afterSale.getUserId());
        refund.setRefundNo(generateRefundNo());
        refund.setRefundAmount(afterSale.getRefundAmount());
        refund.setStatus(RefundStatusEnum.PENDING.getCode());
        refund.setCreateTime(new Date());
        refund.setUpdateTime(new Date());
        this.save(refund);
        return refund;
    }

    private String generateRefundNo() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100_000, 999_999);
        return "RF" + now + rand;
    }
}




