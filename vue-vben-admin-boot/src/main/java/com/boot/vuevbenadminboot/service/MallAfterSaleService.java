package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleRequest;

/**
* @author quannnn
* @description 针对表【mall_after_sale(订单售后表)】的数据库操作Service
* @createDate 2026-07-02 15:59:37
*/
public interface MallAfterSaleService extends IService<MallAfterSale> {
    MallAfterSale createAfterSale(AfterSaleRequest request, String userName);
}
