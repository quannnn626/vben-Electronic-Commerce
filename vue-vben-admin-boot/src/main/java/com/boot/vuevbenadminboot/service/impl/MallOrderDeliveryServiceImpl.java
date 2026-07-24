package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderDelivery;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import com.boot.vuevbenadminboot.service.MallOrderDeliveryService;
import com.boot.vuevbenadminboot.mapper.MallOrderDeliveryMapper;
import com.boot.vuevbenadminboot.service.MallOrderItemService;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.web.dto.req.DeliveryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author quannnn
 * @description 针对表【mall_order_delivery】的数据库操作Service实现
 * @createDate 2026-06-26 14:11:24
 */
@Service
public class MallOrderDeliveryServiceImpl extends ServiceImpl<MallOrderDeliveryMapper, MallOrderDelivery>
        implements MallOrderDeliveryService {
    private final SysUserService sysUserService;
    private final MallOrderService mallOrderService;
    private final MallOrderMapper mallOrderMapper;
    private final MallOrderItemService mallOrderItemService;

    public MallOrderDeliveryServiceImpl(SysUserService sysUserService, MallOrderService mallOrderService,
                                         MallOrderMapper mallOrderMapper, MallOrderItemService mallOrderItemService) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallOrderMapper = mallOrderMapper;
        this.mallOrderItemService = mallOrderItemService;
    }

    @Override
    @Transactional
    public MallOrderDelivery createDelivery(DeliveryRequest req, String username) {
        Long userId = sysUserService.requireUserId(username);
        MallOrder mallOrder = mallOrderMapper.selectOne(
                new LambdaQueryWrapper<MallOrder>()
                        .eq(MallOrder::getOrderNo, req.getOrderNo())
                        .eq(MallOrder::getDeleted, 0)
        );
        if (mallOrder == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!Objects.equals(mallOrder.getStatus(), OrderStatusEnum.PAID.getCode())
                && !Objects.equals(mallOrder.getStatus(), OrderStatusEnum.DELIVERING.getCode())) {
            throw new IllegalArgumentException("仅已支付订单可发货");
        }
        if (req.getTrackingNo() == null || req.getTrackingNo().isBlank()) {
            throw new IllegalArgumentException("请填写物流单号");
        }
        if (req.getLogisticsCompany() == null || req.getLogisticsCompany().isBlank()) {
            throw new IllegalArgumentException("请填写物流公司");
        }
        // 防重：已发货的商品不能再发
        Long count = this.count(new LambdaQueryWrapper<MallOrderDelivery>()
                .eq(MallOrderDelivery::getOrderItemId, req.getOrderItemId())
                .eq(MallOrderDelivery::getDeleted, 0));
        if (count > 0) {
            throw new IllegalArgumentException("该商品已发货");
        }
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setOrderId(mallOrder.getId());
        mallOrderDelivery.setOrderItemId(req.getOrderItemId());
        mallOrderDelivery.setLogisticsCompany(req.getLogisticsCompany());
        mallOrderDelivery.setTrackingNo(req.getTrackingNo());
        mallOrderDelivery.setDeliveryStatus(0);
        mallOrderDelivery.setDeliveryRemark(req.getMessage());
        mallOrderDelivery.setDeliveryUser(userId);
        mallOrderDelivery.setCreateTime(new Date());
        mallOrderDelivery.setUpdateTime(new Date());
        mallOrderDelivery.setDeleted(0);
        this.save(mallOrderDelivery);

        // 所有商品都发货后才改订单状态
        List<MallOrderItem> orderItems = mallOrderItemService.list(
                new LambdaQueryWrapper<MallOrderItem>()
                        .eq(MallOrderItem::getOrderId, mallOrder.getId())
                        .eq(MallOrderItem::getDeleted, 0)
        );
        List<MallOrderDelivery> deliveries = this.list(
                new LambdaQueryWrapper<MallOrderDelivery>()
                        .eq(MallOrderDelivery::getOrderId, mallOrder.getId())
                        .eq(MallOrderDelivery::getDeleted, 0)
        );
        Set<Long> deliveredItemIds = deliveries.stream()
                .map(MallOrderDelivery::getOrderItemId).collect(Collectors.toSet());
        boolean allDelivered = orderItems.stream().allMatch(it -> deliveredItemIds.contains(it.getId()));
        Date now = new Date();
        mallOrder.setStatus(allDelivered
                ? OrderStatusEnum.SHIPPED.getCode()
                : OrderStatusEnum.DELIVERING.getCode());
        mallOrder.setDeliveryTime(now);
        mallOrder.setUpdateTime(now);
        mallOrderMapper.updateById(mallOrder);

        return mallOrderDelivery;
    }

    @Override
    public List<Long> getDeliveredItemIds(Long orderId) {
        return this.list(new LambdaQueryWrapper<MallOrderDelivery>()
                        .eq(MallOrderDelivery::getOrderId, orderId)
                        .eq(MallOrderDelivery::getDeleted, 0))
                .stream()
                .map(MallOrderDelivery::getOrderItemId)
                .filter(id -> id != null)
                .toList();
    }
}




