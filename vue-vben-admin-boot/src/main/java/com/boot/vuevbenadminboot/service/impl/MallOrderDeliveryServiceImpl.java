package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderDelivery;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import com.boot.vuevbenadminboot.service.MallOrderDeliveryService;
import com.boot.vuevbenadminboot.mapper.MallOrderDeliveryMapper;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.web.dto.req.DeliveryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    public MallOrderDeliveryServiceImpl(SysUserService sysUserService, MallOrderService mallOrderService, MallOrderMapper mallOrderMapper) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallOrderMapper = mallOrderMapper;
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
        MallOrderDelivery mallOrderDelivery = new MallOrderDelivery();
        mallOrderDelivery.setOrderId(mallOrder.getId());
        mallOrderDelivery.setLogisticsCompany(req.getLogisticsCompany());
        mallOrderDelivery.setTrackingNo(req.getTrackingNo());
        mallOrderDelivery.setDeliveryStatus(0);
        mallOrderDelivery.setDeliveryRemark(req.getMessage());
        mallOrderDelivery.setDeliveryUser(userId);
        mallOrderDelivery.setCreateTime(new Date());
        mallOrderDelivery.setUpdateTime(new Date());
        mallOrderDelivery.setDeleted(0);
        this.save(mallOrderDelivery);

        Date now = new Date();
        mallOrder.setStatus(OrderStatusEnum.SHIPPED.getCode());
        mallOrder.setDeliveryTime(now);
        mallOrder.setUpdateTime(now);
        mallOrderMapper.updateById(mallOrder);

        return mallOrderDelivery;
    }
}




