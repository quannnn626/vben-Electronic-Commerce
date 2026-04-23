package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallProduct;
import com.boot.vuevbenadminboot.domain.MallProductStock;
import com.boot.vuevbenadminboot.mapper.MallProductMapper;
import com.boot.vuevbenadminboot.mapper.MallProductStockMapper;
import com.boot.vuevbenadminboot.service.MallProductStockService;
import com.boot.vuevbenadminboot.service.dto.StockManageItemDto;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author quannnn
* @description 针对表【mall_product_stock(商品库存表)】的数据库操作Service实现
* @createDate 2026-04-23 13:48:03
*/
@Service
public class MallProductStockServiceImpl extends ServiceImpl<MallProductStockMapper, MallProductStock>
    implements MallProductStockService{

    private final MallProductMapper productMapper;

    public MallProductStockServiceImpl(MallProductMapper productMapper) {
        this.productMapper = productMapper;
    }

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

    @Override
    public List<StockManageItemDto> listForManage(String keyword) {
        LambdaQueryWrapper<MallProduct> wrapper = new LambdaQueryWrapper<MallProduct>()
                .eq(MallProduct::getDeleted, 0)
                .orderByDesc(MallProduct::getId);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(MallProduct::getName, keyword.trim());
        }
        List<MallProduct> products = productMapper.selectList(wrapper);
        if (products.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = products.stream().map(MallProduct::getId).toList();
        Map<Long, MallProductStock> stockMap = getStockMapByProductIds(productIds);
        List<StockManageItemDto> result = new java.util.ArrayList<>();
        for (MallProduct product : products) {
            MallProductStock stock = stockMap.get(product.getId());
            int available = stock == null || stock.getStock() == null ? 0 : stock.getStock();
            int locked = stock == null || stock.getLockedStock() == null ? 0 : stock.getLockedStock();
            StockManageItemDto dto = new StockManageItemDto();
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setAvailableStock(available);
            dto.setLockedStock(locked);
            dto.setTotalStock(available + locked);
            dto.setUpdateTime(stock == null ? product.getUpdateTime() : stock.getUpdateTime());
            result.add(dto);
        }
        return result;
    }

    @Override
    public void increaseStock(Long productId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallProductStock stock = getOrCreateStock(productId);
        stock.setStock((stock.getStock() == null ? 0 : stock.getStock()) + qty);
        stock.setUpdateTime(new Date());
        this.saveOrUpdate(stock);
    }

    @Override
    public void decreaseStock(Long productId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallProductStock stock = getOrCreateStock(productId);
        int current = stock.getStock() == null ? 0 : stock.getStock();
        if (current < qty) {
            throw new IllegalArgumentException("可用库存不足");
        }
        stock.setStock(current - qty);
        stock.setUpdateTime(new Date());
        this.saveOrUpdate(stock);
    }

    @Override
    public void lockStock(Long productId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallProductStock stock = getOrCreateStock(productId);
        int available = stock.getStock() == null ? 0 : stock.getStock();
        int locked = stock.getLockedStock() == null ? 0 : stock.getLockedStock();
        if (available < qty) {
            throw new IllegalArgumentException("可用库存不足，无法锁定");
        }
        stock.setStock(available - qty);
        stock.setLockedStock(locked + qty);
        stock.setUpdateTime(new Date());
        this.saveOrUpdate(stock);
    }

    @Override
    public void unlockStock(Long productId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallProductStock stock = getOrCreateStock(productId);
        int available = stock.getStock() == null ? 0 : stock.getStock();
        int locked = stock.getLockedStock() == null ? 0 : stock.getLockedStock();
        if (locked < qty) {
            throw new IllegalArgumentException("锁定库存不足，无法解锁");
        }
        stock.setLockedStock(locked - qty);
        stock.setStock(available + qty);
        stock.setUpdateTime(new Date());
        this.saveOrUpdate(stock);
    }

    private int normalizeQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        return quantity;
    }

    private MallProductStock getOrCreateStock(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        MallProduct product = productMapper.selectById(productId);
        if (product == null || Objects.equals(product.getDeleted(), 1)) {
            throw new IllegalArgumentException("商品不存在");
        }
        MallProductStock stock = this.getOne(
                new LambdaQueryWrapper<MallProductStock>()
                        .eq(MallProductStock::getProductId, productId)
                        .last("limit 1")
        );
        if (stock == null) {
            stock = new MallProductStock();
            stock.setProductId(productId);
            stock.setStock(0);
            stock.setLockedStock(0);
            stock.setCreateTime(new Date());
            stock.setUpdateTime(new Date());
        }
        return stock;
    }
}




