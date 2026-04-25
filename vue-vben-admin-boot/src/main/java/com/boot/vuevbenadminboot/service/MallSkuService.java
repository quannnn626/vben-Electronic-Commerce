package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallSku;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.StockManageItemDto;

import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_sku(商品SKU表)】的数据库操作Service
* @createDate 2026-04-24 16:39:05
*/
public interface MallSkuService extends IService<MallSku> {
    List<StockManageItemDto> listForManage(String keyword);

    void increaseStock(Long skuId, Integer quantity);

    void decreaseStock(Long skuId, Integer quantity);

    void lockStock(Long skuId, Integer quantity);

    void unlockStock(Long skuId, Integer quantity);
}
