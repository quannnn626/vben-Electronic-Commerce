package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import com.boot.vuevbenadminboot.mapper.SysUserMapper;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.service.MallOrderItemService;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.web.dto.OrderItemDto;
import com.boot.vuevbenadminboot.web.dto.OrderListItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder>
    implements MallOrderService{

    private final SysUserMapper sysUserMapper;
    private final MallOrderItemService orderItemService;
    private final MallSkuService skuService;
    private final MallFileService fileService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MallOrderServiceImpl(
            SysUserMapper sysUserMapper,
            MallOrderItemService orderItemService,
            MallSkuService skuService,
            MallFileService fileService) {
        this.sysUserMapper = sysUserMapper;
        this.orderItemService = orderItemService;
        this.skuService = skuService;
        this.fileService = fileService;
    }

    @Override
    public List<OrderListItemDto> listOrders(String username) {
        Long userId = requireUserId(username);
        List<MallOrder> orders = this.list(
                new LambdaQueryWrapper<MallOrder>()
                        .eq(MallOrder::getUserId, userId)
                        .eq(MallOrder::getDeleted, 0)
                        .orderByDesc(MallOrder::getId)
        );
        if (orders.isEmpty()) {
            return List.of();
        }
        List<Long> orderIds = orders.stream().map(MallOrder::getId).toList();
        Map<Long, List<MallOrderItem>> itemMap = buildItemMap(orderIds);

        Set<Long> productIds = itemMap.values().stream()
                .flatMap(List::stream)
                .map(MallOrderItem::getProductId)
                .collect(Collectors.toSet());
        Map<Long, String> productImageMap = buildProductImageMap(productIds);

        List<OrderListItemDto> result = new ArrayList<>();
        for (MallOrder order : orders) {
            OrderListItemDto dto = new OrderListItemDto();
            dto.setId(order.getId());
            dto.setOrderNo(order.getOrderNo());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPayAmount(order.getPayAmount());
            dto.setStatus(order.getStatus());
            dto.setCreateTime(order.getCreateTime());
            dto.setPayTime(order.getPayTime());
            dto.setDeliveryTime(order.getDeliveryTime());
            dto.setFinishTime(order.getFinishTime());

            List<MallOrderItem> items = itemMap.getOrDefault(order.getId(), List.of());
            List<OrderItemDto> itemDtos = new ArrayList<>();
            for (MallOrderItem item : items) {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProductId());
                itemDto.setProductName(item.getProductName());
                itemDto.setProductImage(productImageMap.get(item.getProductId()));
                itemDto.setPrice(item.getPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setTotalPrice(item.getTotalPrice());
                itemDtos.add(itemDto);
            }
            dto.setItems(itemDtos);
            result.add(dto);
        }
        return result;
    }

    private Long requireUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("未登录");
        }
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return user.getId();
    }

    private Map<Long, List<MallOrderItem>> buildItemMap(List<Long> orderIds) {
        List<MallOrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<MallOrderItem>()
                        .in(MallOrderItem::getOrderId, orderIds)
                        .eq(MallOrderItem::getDeleted, 0)
        );
        return items.stream().collect(Collectors.groupingBy(MallOrderItem::getOrderId));
    }

    private Map<Long, String> buildProductImageMap(Set<Long> productIds) {
        if (productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MallSku> skus = skuService.list(
                new LambdaQueryWrapper<MallSku>()
                        .in(MallSku::getProductId, productIds)
                        .eq(MallSku::getStatus, 1)
                        .orderByAsc(MallSku::getId)
        );
        Map<Long, Long> productFileIdMap = new LinkedHashMap<>();
        for (MallSku sku : skus) {
            if (productFileIdMap.containsKey(sku.getProductId())) {
                continue;
            }
            Long fileId = parseFileId(sku.getSpecData());
            if (fileId != null) {
                productFileIdMap.put(sku.getProductId(), fileId);
            }
        }
        if (productFileIdMap.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MallFile> files = fileService.listByIds(productFileIdMap.values());
        Map<Long, String> filePathMap = files.stream()
                .collect(Collectors.toMap(MallFile::getId, MallFile::getFilePath, (a, b) -> a));

        Map<Long, String> result = new LinkedHashMap<>();
        for (Map.Entry<Long, Long> entry : productFileIdMap.entrySet()) {
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
            return objectMapper.readValue(String.valueOf(specData), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
