package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.enums.AfterSaleReasonEnum;
import com.boot.vuevbenadminboot.domain.enums.AfterSaleStatusEnum;
import com.boot.vuevbenadminboot.domain.enums.AfterSaleTypeEnum;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.mapper.MallOrderItemMapper;
import com.boot.vuevbenadminboot.service.MallAfterSaleService;
import com.boot.vuevbenadminboot.mapper.MallAfterSaleMapper;
import com.boot.vuevbenadminboot.service.MallOrderItemService;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.MallRefundService;
import com.boot.vuevbenadminboot.service.MallResourceRelService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleAuditRequest;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleBatchAuditRequest;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleItemRequest;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleRequest;
import com.boot.vuevbenadminboot.web.dto.resp.AfterSaleAdminListDto;
import com.boot.vuevbenadminboot.web.dto.resp.AfterSaleDetailDto;
import com.boot.vuevbenadminboot.web.dto.resp.OrderItemDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author quannnn
 * @description 针对表【mall_after_sale(订单售后表)】的数据库操作Service实现
 * @createDate 2026-07-02 15:59:37
 */
@Service
public class MallAfterSaleServiceImpl extends ServiceImpl<MallAfterSaleMapper, MallAfterSale>
        implements MallAfterSaleService {
    private final SysUserService sysUserService;
    private final MallOrderService mallOrderService;
    private final MallOrderItemService mallOrderItemService;
    private final MallOrderItemMapper mallOrderItemMapper;
    private final MallAfterSaleMapper mallAfterSaleMapper;
    private final MallResourceRelService resourceRelService;
    private final MallRefundService refundService;

    public MallAfterSaleServiceImpl(SysUserService sysUserService,
                                    MallOrderService mallOrderService,
                                    MallOrderItemService mallOrderItemService,
                                    MallOrderItemMapper mallOrderItemMapper,
                                    MallAfterSaleMapper mallAfterSaleMapper,
                                    MallResourceRelService resourceRelService,
                                    MallRefundService refundService) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallOrderItemService = mallOrderItemService;
        this.mallOrderItemMapper = mallOrderItemMapper;
        this.mallAfterSaleMapper = mallAfterSaleMapper;
        this.resourceRelService = resourceRelService;
        this.refundService = refundService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallAfterSale createAfterSale(AfterSaleRequest request, String userName) {
        // 校验
        Long userId = sysUserService.requireUserId(userName);
        MallOrder mallOrder = mallOrderService.getById(request.getOrderId());
        if (mallOrder == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!mallOrder.getUserId().equals(userId)) {
            throw new IllegalArgumentException("订单不属于当前用户");
        }
        if (mallOrder.getStatus().equals(OrderStatusEnum.WAIT_PAY.getCode())) {
            throw new IllegalArgumentException("订单未支付，不能申请售后");
        }
        if (mallOrder.getStatus().equals(OrderStatusEnum.CANCELLED.getCode())) {
            throw new IllegalArgumentException("订单已取消，不能申请售后");
        }
        // 校验售后类型
        AfterSaleTypeEnum typeEnum = AfterSaleTypeEnum.of(request.getType());
        if (typeEnum == null) {
            throw new IllegalArgumentException("售后类型无效");
        }
        // 校验是否可申请
        validateAfterSaleType(typeEnum, mallOrder.getStatus());
        AfterSaleReasonEnum reasonEnum = AfterSaleReasonEnum.of(request.getReason());
        if (reasonEnum == null) {
            throw new IllegalArgumentException("售后原因无效");
        }
        BigDecimal refundAmount = calcRefundAmount(typeEnum, mallOrder.getPayAmount());

        // 校验商品列表
        List<AfterSaleItemRequest> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("请选择售后商品");
        }

        MallAfterSale first = null;
        for (AfterSaleItemRequest reqItem : items) {
            MallOrderItem orderItem = mallOrderItemMapper.selectOne(
                    new LambdaQueryWrapper<MallOrderItem>()
                            .eq(MallOrderItem::getOrderId, request.getOrderId())
                            .eq(MallOrderItem::getId, reqItem.getOrderItemId())
            );
            if (orderItem == null) {
                throw new IllegalArgumentException("商品不存在");
            }
            Integer qty = reqItem.getQuantity();
            if (qty == null || qty <= 0) {
                throw new IllegalArgumentException("售后数量必须大于0");
            }
            if (qty > orderItem.getQuantity()) {
                throw new IllegalArgumentException("售后数量不能超过购买数量");
            }
            // 累加校验 查询订单项所有进行中的售后
            List<MallAfterSale> activeSales = mallAfterSaleMapper.selectList(
                    new LambdaQueryWrapper<MallAfterSale>()
                            .eq(MallAfterSale::getOrderItemId, reqItem.getOrderItemId())
                            .in(MallAfterSale::getStatus,
                                    AfterSaleStatusEnum.APPLYING.getCode(),
                                    AfterSaleStatusEnum.APPROVED.getCode(),
                                    AfterSaleStatusEnum.REFUNDING.getCode(),
                                    AfterSaleStatusEnum.WAIT_RETURN.getCode()
                            )
            );
            int total = activeSales.stream().mapToInt(s -> s.getQuantity() != null ? s.getQuantity() : 0).sum();
            if (total + qty > orderItem.getQuantity()) {
                throw new IllegalArgumentException("累计售后数量超过购买数量");
            }

            MallAfterSale afterSale = new MallAfterSale();
            afterSale.setAfterSaleNo(generateAfterSaleNo());
            afterSale.setOrderId(request.getOrderId());
            afterSale.setOrderItemId(reqItem.getOrderItemId());
            afterSale.setQuantity(qty);
            afterSale.setUserId(userId);
            afterSale.setType(request.getType());
            afterSale.setReason(request.getReason());
            afterSale.setDescription(request.getDescription());
            afterSale.setRefundAmount(refundAmount);
            afterSale.setStatus(AfterSaleStatusEnum.APPLYING.getCode());
            afterSale.setCreateTime(new Date());
            afterSale.setUpdateTime(new Date());
            afterSale.setDeleted(0);
            this.save(afterSale);

            // 凭证附件仅关联第一条
            List<Long> fileIds = request.getFileIds();
            if (first == null && fileIds != null && !fileIds.isEmpty()) {
                resourceRelService.attachBatch("after_sale", afterSale.getId(), fileIds, "proof");
            }
            if (first == null) {
                first = afterSale;
            }
        }
        return first;
    }

    @Override
    public AfterSaleDetailDto getAfterSaleDetail(Long id, String userName) {
        // 校验
        Long userId = sysUserService.requireUserId(userName);
        MallAfterSale as = this.getById(id);
        if (as == null || !as.getUserId().equals(userId) || as.getDeleted() == 1) {
            throw new IllegalArgumentException("售后单不存在");
        }
        MallOrder order = mallOrderService.getById(as.getOrderId());
        List<MallOrderItem> items = mallOrderItemService.list(
                new LambdaQueryWrapper<MallOrderItem>()
                        .eq(MallOrderItem::getOrderId, as.getOrderId())
                        .eq(MallOrderItem::getDeleted, 0)
        );

        // 组装售后详情
        AfterSaleDetailDto dto = new AfterSaleDetailDto();
        dto.setId(as.getId());
        dto.setAfterSaleNo(as.getAfterSaleNo());
        dto.setOrderId(as.getOrderId());
        dto.setOrderItemId(as.getOrderItemId());
        dto.setType(as.getType());
        dto.setQuantity(as.getQuantity());
        dto.setStatus(as.getStatus());
        dto.setReason(as.getReason());
        dto.setReasonDesc(AfterSaleReasonEnum.of(as.getReason()).getDesc());
        dto.setDescription(as.getDescription());
        dto.setRefundAmount(as.getRefundAmount());
        dto.setApplyTime(as.getCreateTime());
        dto.setAuditTime(as.getAuditTime());

        // 填充订单信息
        if (order != null) {
            dto.setOrderNo(order.getOrderNo());
            dto.setUserId(order.getUserId());
            dto.setPayAmount(order.getPayAmount());
            dto.setReceiverName(order.getReceiverName());
            dto.setReceiverPhone(order.getReceiverPhone());
            dto.setReceiverAddress(order.getReceiverAddress());

            // 转换订单商品列表
            List<com.boot.vuevbenadminboot.web.dto.resp.OrderItemDto> itemDtos = new ArrayList<>();
            for (MallOrderItem item : items) {
                com.boot.vuevbenadminboot.web.dto.resp.OrderItemDto itemDto = new com.boot.vuevbenadminboot.web.dto.resp.OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setSkuId(item.getSkuId());
                itemDto.setProductName(item.getProductName());
                itemDto.setProductImage(item.getProductImage());
                itemDto.setPrice(item.getPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setTotalPrice(item.getTotalPrice());
                itemDtos.add(itemDto);
            }
            dto.setItems(itemDtos);
        }
        return dto;
    }

    @Override
    public List<AfterSaleDetailDto> listAfterSales(String userName) {
        // 校验
        Long userId = sysUserService.requireUserId(userName);
        List<MallAfterSale> afterSales = this.list(
                new LambdaQueryWrapper<MallAfterSale>()
                        .eq(MallAfterSale::getUserId, userId)
                        .eq(MallAfterSale::getDeleted, 0)
                        .orderByDesc(MallAfterSale::getId)
        );
        if (afterSales.isEmpty()) {
            return List.of();
        }

        // 批量查询关联订单构建 orderId -> MallOrder 映射
        List<Long> orderIds = afterSales.stream().map(MallAfterSale::getOrderId).distinct().toList();
        Map<Long, MallOrder> orderMap = mallOrderService.listByIds(orderIds).stream()
                .collect(Collectors.toMap(MallOrder::getId, o -> o, (a, b) -> a));

        // 批量查询关联订单商品，构建 orderId -> List<MallOrderItem> 映射
        Map<Long, List<MallOrderItem>> itemMap = new LinkedHashMap<>();
        for (Long orderId : orderIds) {
            List<MallOrderItem> items = mallOrderItemService.list(
                    new LambdaQueryWrapper<MallOrderItem>()
                            .eq(MallOrderItem::getOrderId, orderId)
                            .eq(MallOrderItem::getDeleted, 0)
            );
            itemMap.put(orderId, items);
        }

        // 逐条组装售后列表DTO
        List<AfterSaleDetailDto> result = new ArrayList<>();
        for (MallAfterSale as : afterSales) {
            AfterSaleDetailDto dto = new AfterSaleDetailDto();
            dto.setId(as.getId());
            dto.setAfterSaleNo(as.getAfterSaleNo());
            dto.setOrderId(as.getOrderId());
            dto.setOrderItemId(as.getOrderItemId());
            dto.setType(as.getType());
            dto.setQuantity(as.getQuantity());
            dto.setStatus(as.getStatus());
            dto.setReason(as.getReason());
            dto.setReasonDesc(AfterSaleReasonEnum.of(as.getReason()).getDesc());
            dto.setDescription(as.getDescription());
            dto.setRefundAmount(as.getRefundAmount());
            dto.setApplyTime(as.getCreateTime());
            dto.setAuditTime(as.getAuditTime());

            // 填充订单信息
            MallOrder order = orderMap.get(as.getOrderId());
            if (order != null) {
                dto.setOrderNo(order.getOrderNo());
                dto.setUserId(order.getUserId());
                dto.setPayAmount(order.getPayAmount());
                dto.setReceiverName(order.getReceiverName());
                dto.setReceiverPhone(order.getReceiverPhone());
                dto.setReceiverAddress(order.getReceiverAddress());

                // 转换订单商品列表
                List<MallOrderItem> items = itemMap.getOrDefault(order.getId(), List.of());
                List<OrderItemDto> itemDtos = new ArrayList<>();
                for (MallOrderItem item : items) {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setId(item.getId());
                    itemDto.setSkuId(item.getSkuId());
                    itemDto.setProductName(item.getProductName());
                    itemDto.setProductImage(item.getProductImage());
                    itemDto.setPrice(item.getPrice());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setTotalPrice(item.getTotalPrice());
                    itemDtos.add(itemDto);
                }
                dto.setItems(itemDtos);
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<AfterSaleAdminListDto> listAfterSalesAdmin() {
        List<MallAfterSale> afterSales = this.list(
                new LambdaQueryWrapper<MallAfterSale>()
                        .eq(MallAfterSale::getDeleted, 0)
                        .orderByDesc(MallAfterSale::getId)
        );
        if (afterSales.isEmpty()) {
            return List.of();
        }
        // 查关联订单
        List<Long> orderIds = afterSales.stream().map(MallAfterSale::getOrderId).distinct().toList();
        Map<Long, MallOrder> orderMap = mallOrderService.listByIds(orderIds).stream()
                .collect(Collectors.toMap(MallOrder::getId, o -> o, (a, b) -> a));
        // 查用户名
        List<Long> userIds = afterSales.stream().map(MallAfterSale::getUserId).distinct().toList();
        Map<Long, String> usernameMap = sysUserService.listByIds(userIds).stream()
                .collect(Collectors.toMap(
                        u -> u.getId(),
                        u -> u.getNickname() != null && !u.getNickname().isBlank() ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));
        // 查订单商品
        Map<Long, List<MallOrderItem>> itemMap = new LinkedHashMap<>();
        for (Long orderId : orderIds) {
            List<MallOrderItem> items = mallOrderItemService.list(
                    new LambdaQueryWrapper<MallOrderItem>()
                            .eq(MallOrderItem::getOrderId, orderId)
                            .eq(MallOrderItem::getDeleted, 0)
            );
            itemMap.put(orderId, items);
        }

        List<AfterSaleAdminListDto> result = new ArrayList<>();
        for (MallAfterSale as : afterSales) {
            AfterSaleAdminListDto dto = new AfterSaleAdminListDto();
            dto.setId(as.getId());
            dto.setAfterSaleNo(as.getAfterSaleNo());
            dto.setType(as.getType());
            dto.setStatus(as.getStatus());
            dto.setRefundAmount(as.getRefundAmount());
            dto.setCreateTime(as.getCreateTime());

            MallOrder order = orderMap.get(as.getOrderId());
            if (order != null) {
                dto.setOrderNo(order.getOrderNo());
                dto.setUsername(usernameMap.getOrDefault(as.getUserId(), "-"));

                List<MallOrderItem> items = itemMap.getOrDefault(order.getId(), List.of());
                List<OrderItemDto> itemDtos = new ArrayList<>();
                for (MallOrderItem item : items) {
                    OrderItemDto itemDto = new OrderItemDto();
                    itemDto.setId(item.getId());
                    itemDto.setSkuId(item.getSkuId());
                    itemDto.setProductName(item.getProductName());
                    itemDto.setProductImage(item.getProductImage());
                    itemDto.setPrice(item.getPrice());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setTotalPrice(item.getTotalPrice());
                    itemDtos.add(itemDto);
                }
                dto.setItems(itemDtos);
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(AfterSaleAuditRequest request, String userName) {
        Long userId = sysUserService.requireUserId(userName);
        MallAfterSale as = this.getById(request.getId());
        if (as == null || as.getDeleted() == 1) {
            throw new IllegalArgumentException("售后单不存在");
        }
        if (!as.getStatus().equals(AfterSaleStatusEnum.APPLYING.getCode())) {
            throw new IllegalArgumentException("当前状态不可审核");
        }
        AfterSaleStatusEnum targetStatus = AfterSaleStatusEnum.of(request.getStatus());
        if (targetStatus != AfterSaleStatusEnum.APPROVED && targetStatus != AfterSaleStatusEnum.REJECTED) {
            throw new IllegalArgumentException("审核状态无效");
        }
        applyAuditResult(as, targetStatus, userId, request.getAuditRemark());
        this.updateById(as);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(AfterSaleBatchAuditRequest request, String userName) {
        Long userId = sysUserService.requireUserId(userName);
        List<AfterSaleBatchAuditRequest.AuditItem> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("请选择售后单");
        }
        for (AfterSaleBatchAuditRequest.AuditItem item : items) {
            MallAfterSale as = this.getById(item.getId());
            if (as == null || as.getDeleted() == 1) continue;
            if (!as.getStatus().equals(AfterSaleStatusEnum.APPLYING.getCode())) continue;
            AfterSaleStatusEnum target = AfterSaleStatusEnum.of(item.getStatus());
            if (target != AfterSaleStatusEnum.APPROVED && target != AfterSaleStatusEnum.REJECTED) continue;
            applyAuditResult(as, target, userId, item.getAuditRemark());
            this.updateById(as);
        }
    }

    /**
     * 审核分流：根据售后类型和审核结果设置对应状态
     */
    private void applyAuditResult(MallAfterSale as, AfterSaleStatusEnum target, Long auditUser, String remark) {
        as.setAuditUser(auditUser);
        as.setAuditTime(new Date());
        as.setAuditRemark(remark);
        as.setUpdateTime(new Date());
        if (target == AfterSaleStatusEnum.REJECTED) {
            as.setStatus(AfterSaleStatusEnum.REJECTED.getCode());
            return;
        }
        AfterSaleTypeEnum type = AfterSaleTypeEnum.of(as.getType());
        if (type == null) return;
        switch (type) {
            case REFUND_ONLY:
                as.setStatus(AfterSaleStatusEnum.REFUNDING.getCode());
                refundService.createRefund(as);
                break;
            case REFUND_RETURN:
                as.setStatus(AfterSaleStatusEnum.WAIT_RETURN.getCode());
                break;
            case EXCHANGE:
                as.setStatus(AfterSaleStatusEnum.APPROVED.getCode());
                break;
        }
    }

    /**
     * 校验售后类型是否适用于当前订单状态
     */
    private void validateAfterSaleType(AfterSaleTypeEnum type, Integer orderStatus) {
        boolean valid;
        switch (type) {
            case REFUND_ONLY:
                valid = orderStatus.equals(OrderStatusEnum.PAID.getCode());
                break;
            case REFUND_RETURN:
            case EXCHANGE:
                valid = orderStatus.equals(OrderStatusEnum.SHIPPED.getCode())
                        || orderStatus.equals(OrderStatusEnum.COMPLETED.getCode());
                break;
            default:
                throw new IllegalArgumentException("不支持的售后类型");
        }
        if (!valid) {
            throw new IllegalArgumentException("当前订单状态不可申请该售后类型");
        }
    }

    /**
     * 计算退款金额
     */
    private BigDecimal calcRefundAmount(AfterSaleTypeEnum type, BigDecimal payAmount) {
        if (type == AfterSaleTypeEnum.EXCHANGE) {
            return BigDecimal.ZERO;
        }
        return payAmount != null ? payAmount : BigDecimal.ZERO;
    }

    /**
     * 生成售后单号：AS + 年月日时分秒 + 6位随机数
     */
    private String generateAfterSaleNo() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100_000, 999_999);
        return "AS" + now + rand;
    }
}
