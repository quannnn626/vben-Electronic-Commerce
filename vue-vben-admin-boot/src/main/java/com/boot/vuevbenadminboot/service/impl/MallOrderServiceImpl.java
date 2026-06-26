package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallCart;
import com.boot.vuevbenadminboot.domain.MallFile;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderDelivery;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.domain.MallUserAddress;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.mapper.MallOrderDeliveryMapper;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import com.boot.vuevbenadminboot.service.MallCartService;
import com.boot.vuevbenadminboot.service.MallFileService;
import com.boot.vuevbenadminboot.service.MallOrderItemService;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.service.MallUserAddressService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.web.dto.req.OrderCreateRequest;
import com.boot.vuevbenadminboot.web.dto.req.OrderQueryRequest;
import com.boot.vuevbenadminboot.web.dto.resp.OrderItemDto;
import com.boot.vuevbenadminboot.web.dto.resp.OrderListItemDto;
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
        implements MallOrderService {

    private final SysUserService sysUserService;
    private final MallOrderItemService orderItemService;
    private final MallSkuService skuService;
    private final MallFileService fileService;
    private final MallUserAddressService addressService;
    private final MallCartService cartService;
    private final MallOrderDeliveryMapper deliveryMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MallOrderServiceImpl(
            SysUserService sysUserService,
            MallOrderItemService orderItemService,
            MallSkuService skuService,
            MallFileService fileService,
            MallUserAddressService addressService,
            MallCartService cartService,
            MallOrderDeliveryMapper deliveryMapper) {
        this.sysUserService = sysUserService;
        this.orderItemService = orderItemService;
        this.skuService = skuService;
        this.fileService = fileService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.deliveryMapper = deliveryMapper;
    }

    // 获取订单列表
    @Override
    public List<OrderListItemDto> listOrders(String username) {
        Long userId = sysUserService.requireUserId(username);
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
        Map<Long, MallOrderDelivery> deliveryMap = buildDeliveryMap(orderIds);

        List<OrderListItemDto> result = new ArrayList<>();
        for (MallOrder order : orders) {
            OrderListItemDto dto = buildOrderDto(order, itemMap, skuImageMap);
            fillDelivery(dto, deliveryMap.get(order.getId()));
            result.add(dto);
        }
        return result;
    }

    // 创建订单
    @Override
    @Transactional
    public OrderListItemDto createOrder(String username, OrderCreateRequest req) {
        Long userId = sysUserService.requireUserId(username);

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
        order.setStatus(OrderStatusEnum.WAIT_PAY.getCode());
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

        // 清除购物车中已下单的商品
        Set<Long> orderedSkuIds = items.stream()
                .map(OrderCreateRequest.OrderItemRequest::getSkuId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        cartService.remove(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getUserId, userId)
                .in(MallCart::getSkuId, orderedSkuIds));

        // 组建返回 DTO
        Set<Long> skuIds = items.stream()
                .map(OrderCreateRequest.OrderItemRequest::getSkuId)
                .collect(Collectors.toSet());
        Map<Long, String> imageMap = buildSkuImageMap(skuIds);

        Map<Long, List<MallOrderItem>> itemMap = Map.of(order.getId(), orderItems);
        return buildOrderDto(order, itemMap, imageMap);
    }

    // 取消订单
    @Override
    @Transactional
    public void cancelOrder(String username, Long orderId) {
        Long userId = sysUserService.requireUserId(username);
        MallOrder order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getCode())) {
            throw new IllegalArgumentException("仅待支付订单可取消");
        }
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
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

    // 确认收货
    @Override
    @Transactional
    public void finishOrder(String username, Long orderId) {
        Long userId = sysUserService.requireUserId(username);
        MallOrder order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!order.getStatus().equals(OrderStatusEnum.SHIPPED.getCode())) {
            throw new IllegalArgumentException("仅已发货订单可确认收货");
        }
        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        order.setFinishTime(new java.util.Date());
        order.setUpdateTime(new java.util.Date());
        this.updateById(order);
    }

    // 查看订单详情
    @Override
    public OrderListItemDto getOrderDetail(String username, Long orderId) {
        Long userId = sysUserService.requireUserId(username);
        MallOrder order = this.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不存在");
        }
        List<Long> orderIds = List.of(orderId);
        Map<Long, List<MallOrderItem>> itemMap = buildItemMap(orderIds);

        Set<Long> skuIds = itemMap.values().stream()
                .flatMap(List::stream)
                .map(MallOrderItem::getSkuId)
                .collect(Collectors.toSet());
        Map<Long, String> skuImageMap = buildSkuImageMap(skuIds);
        Map<Long, MallOrderDelivery> deliveryMap = buildDeliveryMap(orderIds);
        OrderListItemDto dto = buildOrderDto(order, itemMap, skuImageMap);
        fillDelivery(dto, deliveryMap.get(orderId));
        return dto;
    }

    @Override
    public List<OrderListItemDto> getAllUserList(OrderQueryRequest req) {
        LambdaQueryWrapper<MallOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MallOrder::getDeleted, 0);
        if (req.getOrderNo() != null && !req.getOrderNo().isBlank()) {
            queryWrapper.like(MallOrder::getOrderNo, req.getOrderNo());
        }
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            List<Long> userIds = sysUserService.list(
                    new LambdaQueryWrapper<SysUser>()
                            .like(SysUser::getNickname, req.getUsername())
            ).stream().map(SysUser::getId).toList();
            if (userIds.isEmpty()) {
                return List.of();
            }
            queryWrapper.in(MallOrder::getUserId, userIds);
        }
        if (req.getStatus() != null) {
            queryWrapper.eq(MallOrder::getStatus, req.getStatus());
        }
        if (req.getCreateTime() != null) {
            queryWrapper.ge(MallOrder::getCreateTime, req.getCreateTime());
        }
        if (req.getEndCreateTime() != null) {
            queryWrapper.le(MallOrder::getCreateTime, req.getEndCreateTime());
        }
        queryWrapper.orderByDesc(MallOrder::getId);

        List<MallOrder> orders = this.list(queryWrapper);
        if (orders.isEmpty()) {
            return List.of();
        }
        Set<Long> userIds = orders.stream().map(MallOrder::getUserId).collect(Collectors.toSet());
        Map<Long, String> usernameMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(
                        u -> u.getId(),
                        u -> u.getNickname() != null && !u.getNickname().isBlank()
                                ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));

        List<Long> orderIds = orders.stream().map(MallOrder::getId).toList();
        Map<Long, List<MallOrderItem>> itemMap = buildItemMap(orderIds);

        Set<Long> skuIds = itemMap.values().stream()
                .flatMap(List::stream)
                .map(MallOrderItem::getSkuId)
                .collect(Collectors.toSet());
        Map<Long, String> skuImageMap = buildSkuImageMap(skuIds);
        Map<Long, MallOrderDelivery> deliveryMap = buildDeliveryMap(orderIds);

        List<OrderListItemDto> result = new ArrayList<>();
        for (MallOrder order : orders) {
            OrderListItemDto dto = buildOrderDto(order, itemMap, skuImageMap);
            dto.setUsername(usernameMap.get(order.getUserId()));
            fillDelivery(dto, deliveryMap.get(order.getId()));
            result.add(dto);
        }
        return result;
    }

    // 构建订单详情 DTO
    private OrderListItemDto buildOrderDto(MallOrder order,
                                           Map<Long, List<MallOrderItem>> itemMap,
                                           Map<Long, String> imageMap) {
        OrderListItemDto dto = new OrderListItemDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setOrderNo(order.getOrderNo());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPayAmount(order.getPayAmount());
        dto.setStatus(order.getStatus());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setReceiverAddress(order.getReceiverAddress());
        dto.setCreateTime(order.getCreateTime());
        dto.setPayTime(order.getPayTime());
        dto.setDeliveryTime(order.getDeliveryTime());
        dto.setFinishTime(order.getFinishTime());
        dto.setCancelTime(order.getCancelTime());

        List<MallOrderItem> items = itemMap.getOrDefault(order.getId(), List.of());
        List<OrderItemDto> itemDtos = new ArrayList<>();
        for (MallOrderItem item : items) {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setId(item.getId());
            itemDto.setSkuId(item.getSkuId());
            itemDto.setProductName(item.getProductName());
            itemDto.setProductImage(imageMap.get(item.getSkuId()));
            itemDto.setSkuSpecName(item.getSkuSpecName());
            itemDto.setPrice(item.getPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setTotalPrice(item.getTotalPrice());
            itemDtos.add(itemDto);
        }
        dto.setItems(itemDtos);
        return dto;
    }

    // 构建地址文本，以防止用户修改地址后订单地址被修改
    private String buildAddressText(MallUserAddress addr) {
        StringBuilder sb = new StringBuilder();
        if (addr.getProvince() != null) sb.append(addr.getProvince());
        if (addr.getCity() != null) sb.append(addr.getCity());
        if (addr.getDistrict() != null) sb.append(addr.getDistrict());
        if (addr.getDetailAddress() != null) sb.append(addr.getDetailAddress());
        return sb.toString();
    }

    // 生成唯一订单号
    private String generateOrderNo() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100_000, 999_999);
        return now + rand;
    }

    // 查询订单对应商品
    private Map<Long, List<MallOrderItem>> buildItemMap(List<Long> orderIds) {
        List<MallOrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<MallOrderItem>()
                        .in(MallOrderItem::getOrderId, orderIds)
                        .eq(MallOrderItem::getDeleted, 0)
        );
        return items.stream().collect(Collectors.groupingBy(MallOrderItem::getOrderId));
    }

    // 查询商品对应图片
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

    // 解析规格数据中的文件ID
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

    // 将规格数据转换为Map<String, Object>格式
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
            return objectMapper.readValue(String.valueOf(specData), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    // 批量查询物流信息
    private Map<Long, MallOrderDelivery> buildDeliveryMap(List<Long> orderIds) {
        if (orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MallOrderDelivery> deliveries = deliveryMapper.selectList(
                new LambdaQueryWrapper<MallOrderDelivery>()
                        .in(MallOrderDelivery::getOrderId, orderIds)
                        .eq(MallOrderDelivery::getDeleted, 0)
        );
        return deliveries.stream()
                .collect(Collectors.toMap(MallOrderDelivery::getOrderId, d -> d, (a, b) -> a));
    }

    // 填充物流信息
    private void fillDelivery(OrderListItemDto dto, MallOrderDelivery delivery) {
        if (delivery != null) {
            dto.setLogisticsCompany(delivery.getLogisticsCompany());
            dto.setTrackingNo(delivery.getTrackingNo());
        }
    }
}
