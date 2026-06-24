package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallCart;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.boot.vuevbenadminboot.domain.enums.CommonStatusEnum;
import com.boot.vuevbenadminboot.service.MallCartService;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.mapper.MallCartMapper;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.util.QuantityUtil;
import com.boot.vuevbenadminboot.web.dto.req.CartRequest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.boot.vuevbenadminboot.web.dto.resp.CartItemDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author quannnn
 * @description 针对表【mall_cart(购物车表)】的数据库操作Service实现
 * @createDate 2026-06-23 13:48:57
 */
@Service
public class MallCartServiceImpl extends ServiceImpl<MallCartMapper, MallCart>
        implements MallCartService {
    private final SysUserService sysUserService;
    private final MallSkuService mallSkuService;
    private final MallCartMapper mallCartMapper;
    private final MallFileService mallFileService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MallCartServiceImpl(SysUserService sysUserService,
                               MallSkuService mallSkuService,
                               MallCartMapper mallCartMapper,
                               MallFileService mallFileService) {
        this.sysUserService = sysUserService;
        this.mallSkuService = mallSkuService;
        this.mallCartMapper = mallCartMapper;
        this.mallFileService = mallFileService;
    }

    @Override
    @Transactional
    public MallCart createCart(String username, CartRequest req) {
        Long userId = sysUserService.requireUserId(username);
        int qty = QuantityUtil.requirePositive(req.getQuantity());
        MallSku sku = mallSkuService.getById(req.getSkuId());
        if (sku == null) {
            throw new IllegalArgumentException("该型号不存在");
        }
        if (!Objects.equals(CommonStatusEnum.ENABLED.getCode(), sku.getStatus())) {
            throw new IllegalArgumentException("商品已下架");
        }
        if (sku.getStock() == null || sku.getStock() <= 0) {
            throw new IllegalArgumentException("商品库存不足");
        }
        MallCart existCart = this.getOne(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getUserId, userId)
                .eq(MallCart::getSkuId, req.getSkuId())
        );
        if (existCart != null) {
            int newQty = existCart.getQuantity() + qty;
            if (sku.getStock() < newQty) {
                throw new IllegalArgumentException("库存不足，无法加入购物车");
            }
            existCart.setQuantity(newQty);
            this.updateById(existCart);
            return existCart;
        }
        if (sku.getStock() < qty) {
            throw new IllegalArgumentException("库存不足，无法加入购物车");
        }
        MallCart cart = new MallCart();
        cart.setUserId(userId);
        cart.setProductId(sku.getProductId());
        cart.setSkuId(req.getSkuId());
        cart.setQuantity(qty);
        this.save(cart);
        return cart;
    }

    @Override
    public List<CartItemDto> listCart(String username) {
        Long userId = sysUserService.requireUserId(username);
        List<CartItemDto> cartItemDtos = mallCartMapper.listCartByUserId(userId);
        if (cartItemDtos.isEmpty()) {
            return cartItemDtos;
        }
        Set<Long> skuIds = cartItemDtos.stream()
                .map(CartItemDto::getSkuId)
                .collect(Collectors.toSet());
        Map<Long, MallSku> skuMap = mallSkuService.listByIds(skuIds).stream()
                .collect(Collectors.toMap(MallSku::getId, s -> s, (a, b) -> a));
        Map<Long, String> skuImageMap = buildSkuImageMap(skuMap);
        cartItemDtos.forEach(item -> {
            MallSku sku = skuMap.get(item.getSkuId());
            if (sku != null) {
                item.setSkuName(parseSpecName(sku.getSpecData()));
            }
            item.setProductImage(skuImageMap.get(item.getSkuId()));
            item.setSubtotalAmount(
                    item.getSalePrice().multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    )
            );
        });
        return cartItemDtos;
    }

    private Map<Long, String> buildSkuImageMap(Map<Long, MallSku> skuMap) {
        Map<Long, Long> skuFileIdMap = new LinkedHashMap<>();
        for (MallSku sku : skuMap.values()) {
            Long fileId = parseFileId(sku.getSpecData());
            if (fileId != null) {
                skuFileIdMap.put(sku.getId(), fileId);
            }
        }
        if (skuFileIdMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, String> filePathMap = mallFileService.listByIds(skuFileIdMap.values()).stream()
                .collect(Collectors.toMap(MallFile::getId, MallFile::getFilePath, (a, b) -> a));
        Map<Long, String> result = new LinkedHashMap<>();
        for (Map.Entry<Long, Long> entry : skuFileIdMap.entrySet()) {
            String path = filePathMap.get(entry.getValue());
            if (path != null && !path.isBlank()) {
                result.put(entry.getKey(), path);
            }
        }
        return result;
    }

    private Long parseFileId(Object specData) {
        Map<String, Object> map = parseSpecData(specData);
        Object value = map.get("fileId");
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value != null) {
            try {
                return Long.parseLong(String.valueOf(value));
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private String parseSpecName(Object specData) {
        Map<String, Object> map = parseSpecData(specData);
        return Objects.toString(map.getOrDefault("name", ""), "");
    }

    private Map<String, Object> parseSpecData(Object specData) {
        if (specData == null) {
            return Collections.emptyMap();
        }
        if (specData instanceof Map<?, ?> raw) {
            Map<String, Object> converted = new LinkedHashMap<>();
            raw.forEach((k, v) -> converted.put(String.valueOf(k), v));
            return converted;
        }
        try {
            return objectMapper.readValue(String.valueOf(specData),
                    new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}




