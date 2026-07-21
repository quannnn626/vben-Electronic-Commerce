package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.boot.vuevbenadminboot.domain.MallRefund;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author quannnn
* @description 针对表【mall_refund(退款表)】的数据库操作Service
* @createDate 2026-07-21 10:46:07
*/
public interface MallRefundService extends IService<MallRefund> {

    MallRefund createRefund(MallAfterSale afterSale);
}
