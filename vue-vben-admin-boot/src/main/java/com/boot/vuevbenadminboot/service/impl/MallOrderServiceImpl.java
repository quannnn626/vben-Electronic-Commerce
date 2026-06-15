package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.boot.vuevbenadminboot.domain.MallUserAddress;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import com.boot.vuevbenadminboot.mapper.SysUserMapper;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.service.MallOrderItemService;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.service.MallUserAddressService;
import com.boot.vuevbenadminboot.web.dto.OrderCreateRequest;
import com.boot.vuevbenadminboot.web.dto.OrderItemDto;
import com.boot.vuevbenadminboot.web.dto.OrderListItemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder>
    implements MallOrderService{

    private final SysUserMapper sysUserMapper;
    private final MallOrderItemService orderItemService;
    private final MallSkuService skuService;
    private final MallFileService fileService;
    private final MallUserAddressService addressService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MallOrderServiceImpl(
            SysUserMapper sysUserMapper,
            MallOrderItemService orderItemService,
            MallSkuService skuService,
            MallFileService fileService,
            MallUserAddressService addressService) {
        this.sysUserMapper = sysUserMapper;
        this.orderItemService = orderItemService;
        this.skuService = skuService;
        this.fileService = fileService;
        this.addressService = addressService;
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

        Set<Long> skuIds = itemMap.values().stream()
                .flatMap(List::stream)
                .map(MallOrderItem::getSkuId)
                .collect(Collectors.toSet());
        Map<Long, String> skuImageMap = buildSkuImageMap(skuIds);

        List<OrderListItemDto> result = new ArrayList<>();
        for (MallOrder order : orders) {
            OrderListItemDto dto = buildOrderDto(order, itemMap, skuImageMap);
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public OrderListItemDto createOrder(String username, OrderCreateRequest req) {
        Long userId = requireUserId(username);

        MallUserAddress address = addressService.getById(req.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("收货地址不存在");
        }

        List<OrderCreateRequest.OrderItemRequest> items = req.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("商品不能为空");
        }

        BigDecimal totalAmount = items.stream()
                .map(OrderCreateRequest.OrderItemRequest::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MallOrder order = new MallOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(buildAddressText(address));
        order.setAddressId(address.getId());
        order.setCreateTime(new java.util.Date());
        order.setUpdateTime(new java.util.Date());
        order.setDeleted(0);
        this.save(order);

        List<MallOrderItem> orderItems = new ArrayList<>();
        for (OrderCreateRequest.OrderItemRequest itemReq : items) {
            MallOrderItem item = new MallOrderItem();
            item.setOrderId(order.getId());
            item.setSkuId(itemReq.getSkuId());
            item.setProductName(itemReq.getProductName());
            item.setProductImage(itemReq.getProductImage());
            item.setSkuSpecName(itemReq.getSkuSpecName());
            item.setPrice(itemReq.getPrice());
            item.setQuantity(itemReq.getQuantity());
            item.setTotalPrice(itemReq.getTotalPrice());
            item.setCreateTime(new java.util.Date());
            item.setUpdateTime(new java.util.Date());
            item.setDeleted(0);
            orderItems.add(item);
        }
        orderItemService.saveBatch(orderItems);

        // 锁定SKU库存
        for (OrderCreateRequest.OrderItemRequest itemReq : items) {
            if (itemReq.getSkuId() != null) {
                skuService.lockStock(itemReq.getSkuId(), itemReq.getQuantity());
            }
        }

        // 组建返回 DTO
        Set<Long> skuIds = items.stream()
                .map(OrderCreateRequest.OrderItemRequest::getSkuId)
                .collect(Collectors.toSet());
        Map<Long, String> imageMap = buildSkuImageMap(skuIds);

        Map<Long, List<MallOrderItem>> itemMap = Map.of(order.getId(), orderItems);
        return buildOrderDto(order, itemMap, imageMap);
    }

    @Override
    @Transactional
    public void cancelOrder(String username, Long orderId) {
        Long userId = requireUserId(username);
        MallOrder order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new IllegalArgumentException("仅待支付订单可取消");
        }
        order.setStatus(4);
        order.setCancelTime(new java.util.Date());
        order.setUpdateTime(new java.util.Date());
        this.updateById(order);

        // 释放锁定库存
        List<MallOrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<MallOrderItem>()
                        .eq(MallOrderItem::getOrderId, orderId)
                        .eq(MallOrderItem::getDeleted, 0)
        );
        for (MallOrderItem item : items) {
            if (item.getSkuId() != null) {
                skuService.unlockStock(item.getSkuId(), item.getQuantity());
            }
        }
    }

    @Override
    @Transactional
    public void finishOrder(String username, Long orderId) {
        Long userId = requireUserId(username);
        MallOrder order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new IllegalArgumentException("仅已发货订单可确认收货");
        }
        order.setStatus(3);
        order.setFinishTime(new java.util.Date());
        order.setUpdateTime(new java.util.Date());
        this.updateById(order);
    }

    private OrderListItemDto buildOrderDto(MallOrder order,
                                            Map<Long, List<MallOrderItem>> itemMap,
                                            Map<Long, String> imageMap) {
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
            itemDto.setSkuId(item.getSkuId());
            itemDto.setProductName(item.getProductName());
            itemDto.setProductImage(imageMap.get(item.getSkuId()));
            itemDto.setPrice(item.getPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setTotalPrice(item.getTotalPrice());
            itemDtos.add(itemDto);
        }
        dto.setItems(itemDtos);
        return dto;
    }

    private String buildAddressText(MallUserAddress addr) {
        StringBuilder sb = new StringBuilder();
        if (addr.getProvince() != null) sb.append(addr.getProvince());
        if (addr.getCity() != null) sb.append(addr.getCity());
        if (addr.getDistrict() != null) sb.append(addr.getDistrict());
        if (addr.getDetailAddress() != null) sb.append(addr.getDetailAddress());
        return sb.toString();
    }

    private String generateOrderNo() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100_000, 999_999);
        return now + rand;
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

    private Map<Long, String> buildSkuImageMap(Set<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MallSku> skus = skuService.listByIds(skuIds);
        Map<Long, Long> skuFileIdMap = new LinkedHashMap<>();
        for (MallSku sku : skus) {
            Long fileId = parseFileId(sku.getSpecData());
            if (fileId != null) {
                skuFileIdMap.put(sku.getId(), fileId);
            }
        }
        if (skuFileIdMap.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MallFile> files = fileService.listByIds(skuFileIdMap.values());
        Map<Long, String> filePathMap = files.stream()
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
