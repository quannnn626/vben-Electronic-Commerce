package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallProductStock;
import com.boot.vuevbenadminboot.service.MallProductStockService;
import com.boot.vuevbenadminboot.mapper.MallProductStockMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author quannnn
* @description 针对表【mall_product_stock(商品库存表)】的数据库操作Service实现
* @createDate 2026-04-23 13:48:03
*/
@Service
public class MallProductStockServiceImpl extends ServiceImpl<MallProductStockMapper, MallProductStock>
    implements MallProductStockService{

    @Override
    public Map<Long, MallProductStock> getStockMapByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        return this.list(
                new LambdaQueryWrapper<MallProductStock>()
                        .in(MallProductStock::getProductId, productIds)
        ).stream().collect(Collectors.toMap(MallProductStock::getProductId, item -> item, (a, b) -> a));
    }

    @Override
    public void saveProductStock(Long productId, Integer stockValue) {
        MallProductStock stock = this.getOne(
                new LambdaQueryWrapper<MallProductStock>()
                        .eq(MallProductStock::getProductId, productId)
                        .last("limit 1")
        );
        if (stock == null) {
            stock = new MallProductStock();
            stock.setProductId(productId);
            stock.setLockedStock(0);
            stock.setCreateTime(new Date());
        }
        stock.setStock(stockValue == null ? 0 : stockValue);
        stock.setUpdateTime(new Date());
        this.saveOrUpdate(stock);
    }

    @Override
    public void deleteByProductId(Long productId) {
        this.remove(
                new LambdaQueryWrapper<MallProductStock>()
                        .eq(MallProductStock::getProductId, productId)
        );
    }
}




