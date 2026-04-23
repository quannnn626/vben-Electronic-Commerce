package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallProductStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.service.dto.StockManageItemDto;

import java.util.List;
import java.util.Map;

/**
* @author quannnn
* @description 针对表【mall_product_stock(商品库存表)】的数据库操作Service
* @createDate 2026-04-23 13:48:03
*/
public interface MallProductStockService extends IService<MallProductStock> {
    Map<Long, MallProductStock> getStockMapByProductIds(List<Long> productIds);
    void saveProductStock(Long productId, Integer stockValue);
    void deleteByProductId(Long productId);
    List<StockManageItemDto> listForManage(String keyword);
    void increaseStock(Long productId, Integer quantity);
    void decreaseStock(Long productId, Integer quantity);
    void lockStock(Long productId, Integer quantity);
    void unlockStock(Long productId, Integer quantity);
}
