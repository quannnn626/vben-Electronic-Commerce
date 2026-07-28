package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderDelivery;
import com.boot.vuevbenadminboot.domain.MallOrderDeliveryItem;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.enums.OrderItemStatusEnum;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import com.boot.vuevbenadminboot.service.*;
import com.boot.vuevbenadminboot.mapper.MallOrderDeliveryMapper;
import com.boot.vuevbenadminboot.web.dto.req.DeliveryItemDto;
import com.boot.vuevbenadminboot.web.dto.req.DeliveryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private final MallOrderDeliveryItemService deliveryItemService;

    public MallOrderDeliveryServiceImpl(SysUserService sysUserService, MallOrderService mallOrderService,
                                         MallOrderMapper mallOrderMapper, MallOrderItemService mallOrderItemService,
                                         MallOrderDeliveryItemService deliveryItemService) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallOrderMapper = mallOrderMapper;
        this.mallOrderItemService = mallOrderItemService;
        this.deliveryItemService = deliveryItemService;
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
        if (!Objects.equals(mallOrder.getStatus(), OrderStatusEnum.PAID.getCode())) {
            throw new IllegalArgumentException("仅已支付订单可发货");
        }
        if (req.getTrackingNo() == null || req.getTrackingNo().isBlank()) {
            throw new IllegalArgumentException("请填写物流单号");
        }
        if (req.getLogisticsCompany() == null || req.getLogisticsCompany().isBlank()) {
            throw new IllegalArgumentException("请填写物流公司");
        }

        // 创建物流主记录
        MallOrderDelivery delivery = new MallOrderDelivery();
        delivery.setOrderId(mallOrder.getId());
        delivery.setLogisticsCompany(req.getLogisticsCompany());
        delivery.setTrackingNo(req.getTrackingNo());
        delivery.setDeliveryStatus(0);
        delivery.setDeliveryRemark(req.getMessage());
        delivery.setDeliveryUser(userId);
        delivery.setCreateTime(new Date());
        delivery.setUpdateTime(new Date());
        delivery.setDeleted(0);
        this.save(delivery);

        // 处理发货商品
        List<DeliveryItemDto> items = req.getItems();
        if (items != null && !items.isEmpty()) {
            for (DeliveryItemDto item : items) {
                MallOrderDeliveryItem di = new MallOrderDeliveryItem();
                di.setDeliveryId(delivery.getId());
                di.setOrderItemId(item.getOrderItemId());
                di.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
                deliveryItemService.save(di);
                // 更新商品状态为已发货
                MallOrderItem orderItem = mallOrderItemService.getById(item.getOrderItemId());
                if (orderItem != null) {
                    orderItem.setItemStatus(OrderItemStatusEnum.SHIPPED.getCode());
                    mallOrderItemService.updateById(orderItem);
                }
            }
        }

        Date now = new Date();
        mallOrder.setDeliveryTime(now);
        mallOrder.setUpdateTime(now);
        mallOrderMapper.updateById(mallOrder);

        return delivery;
    }

    @Override
    public List<Long> getDeliveredItemIds(Long orderId) {
        List<Long> deliveryIds = this.list(new LambdaQueryWrapper<MallOrderDelivery>()
                        .eq(MallOrderDelivery::getOrderId, orderId)
                        .eq(MallOrderDelivery::getDeleted, 0))
                .stream().map(MallOrderDelivery::getId).toList();
        if (deliveryIds.isEmpty()) return List.of();
        return deliveryItemService.getDeliveredItemIds(deliveryIds);
    }
}




