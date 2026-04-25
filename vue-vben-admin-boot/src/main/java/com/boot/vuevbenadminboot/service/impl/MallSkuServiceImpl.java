package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallProduct;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.boot.vuevbenadminboot.mapper.MallProductMapper;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.mapper.MallSkuMapper;
import com.boot.vuevbenadminboot.web.dto.StockManageItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author quannnn
* @description 针对表【mall_sku(商品SKU表)】的数据库操作Service实现
* @createDate 2026-04-24 16:39:05
*/
@Service
public class MallSkuServiceImpl extends ServiceImpl<MallSkuMapper, MallSku>
    implements MallSkuService{

    private final MallProductMapper productMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MallSkuServiceImpl(MallProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<StockManageItemDto> listForManage(String keyword) {
        List<MallSku> skus = this.list(
                new LambdaQueryWrapper<MallSku>().orderByDesc(MallSku::getId)
        );
        if (skus.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = skus.stream().map(MallSku::getProductId).filter(Objects::nonNull).distinct().toList();
        Map<Long, MallProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .filter(item -> !Objects.equals(item.getDeleted(), 1))
                .collect(Collectors.toMap(MallProduct::getId, item -> item, (a, b) -> a));

        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<StockManageItemDto> result = new ArrayList<>();
        for (MallSku sku : skus) {
            MallProduct product = productMap.get(sku.getProductId());
            if (product == null) {
                continue;
            }
            String specName = parseSpecName(sku.getSpecData());
            if (!key.isBlank()) {
                String productName = product.getName() == null ? "" : product.getName().toLowerCase();
                String skuCode = sku.getSkuCode() == null ? "" : sku.getSkuCode().toLowerCase();
                String spec = specName.toLowerCase();
                if (!productName.contains(key) && !skuCode.contains(key) && !spec.contains(key)) {
                    continue;
                }
            }
            int available = sku.getStock() == null ? 0 : sku.getStock();
            int locked = sku.getLockedStock() == null ? 0 : sku.getLockedStock();
            StockManageItemDto dto = new StockManageItemDto();
            dto.setSkuId(sku.getId());
            dto.setSkuCode(sku.getSkuCode());
            dto.setSpecName(specName);
            dto.setProductId(product.getId());
            dto.setProductName(product.getName());
            dto.setAvailableStock(available);
            dto.setLockedStock(locked);
            dto.setTotalStock(available + locked);
            dto.setUpdateTime(sku.getUpdateTime());
            result.add(dto);
        }
        return result;
    }

    @Override
    public void increaseStock(Long skuId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallSku sku = getSkuOrThrow(skuId);
        sku.setStock((sku.getStock() == null ? 0 : sku.getStock()) + qty);
        sku.setUpdateTime(new Date());
        this.updateById(sku);
    }

    @Override
    public void decreaseStock(Long skuId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallSku sku = getSkuOrThrow(skuId);
        int available = sku.getStock() == null ? 0 : sku.getStock();
        if (available < qty) {
            throw new IllegalArgumentException("可用库存不足");
        }
        sku.setStock(available - qty);
        sku.setUpdateTime(new Date());
        this.updateById(sku);
    }

    @Override
    public void lockStock(Long skuId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallSku sku = getSkuOrThrow(skuId);
        int available = sku.getStock() == null ? 0 : sku.getStock();
        int locked = sku.getLockedStock() == null ? 0 : sku.getLockedStock();
        if (available < qty) {
            throw new IllegalArgumentException("可用库存不足，无法锁定");
        }
        sku.setStock(available - qty);
        sku.setLockedStock(locked + qty);
        sku.setUpdateTime(new Date());
        this.updateById(sku);
    }

    @Override
    public void unlockStock(Long skuId, Integer quantity) {
        int qty = normalizeQuantity(quantity);
        MallSku sku = getSkuOrThrow(skuId);
        int available = sku.getStock() == null ? 0 : sku.getStock();
        int locked = sku.getLockedStock() == null ? 0 : sku.getLockedStock();
        if (locked < qty) {
            throw new IllegalArgumentException("锁定库存不足，无法解锁");
        }
        sku.setLockedStock(locked - qty);
        sku.setStock(available + qty);
        sku.setUpdateTime(new Date());
        this.updateById(sku);
    }

    private int normalizeQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        return quantity;
    }

    private MallSku getSkuOrThrow(Long skuId) {
        if (skuId == null) {
            throw new IllegalArgumentException("SKU ID不能为空");
        }
        MallSku sku = this.getById(skuId);
        if (sku == null) {
            throw new IllegalArgumentException("SKU不存在");
        }
        MallProduct product = productMapper.selectById(sku.getProductId());
        if (product == null || Objects.equals(product.getDeleted(), 1)) {
            throw new IllegalArgumentException("所属商品不存在");
        }
        return sku;
    }

    private String parseSpecName(Object specData) {
        if (specData == null) {
            return "";
        }
        Map<String, Object> specMap = parseSpecData(specData);
        return Objects.toString(specMap.getOrDefault("name", ""), "");
    }

    private Map<String, Object> parseSpecData(Object specData) {
        if (specData instanceof Map<?, ?> raw) {
            Map<String, Object> converted = new LinkedHashMap<>();
            raw.forEach((k, v) -> converted.put(String.valueOf(k), v));
            return converted;
        }
        try {
            return objectMapper.readValue(String.valueOf(specData), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}




