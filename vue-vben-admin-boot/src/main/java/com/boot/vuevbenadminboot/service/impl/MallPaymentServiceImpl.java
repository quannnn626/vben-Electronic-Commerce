package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.MallPayment;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.domain.enums.PaymentStatusEnum;
import com.boot.vuevbenadminboot.service.*;
import com.boot.vuevbenadminboot.mapper.MallPaymentMapper;
import com.boot.vuevbenadminboot.web.dto.req.PaymentCallbackRequest;
import com.boot.vuevbenadminboot.web.dto.req.PaymentCreateRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MallPaymentServiceImpl extends ServiceImpl<MallPaymentMapper, MallPayment>
        implements MallPaymentService {
    private final SysUserService sysUserService;
    private final MallOrderService mallOrderService;
    private final MallOrderItemService mallOrderItemService;
    private final MallPaymentMapper mallPaymentMapper;
    private final MallSkuService mallSkuService;

    public MallPaymentServiceImpl(SysUserService sysUserService,
                                  MallOrderService mallOrderService,
                                  MallOrderItemService mallOrderItemService,
                                  MallPaymentMapper mallPaymentMapper,
                                  MallSkuService mallSkuService) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallOrderItemService = mallOrderItemService;
        this.mallPaymentMapper = mallPaymentMapper;
        this.mallSkuService = mallSkuService;
    }

    @Override
    @Transactional
    public MallPayment createPayment(String username, PaymentCreateRequest req) {
        Long userId = sysUserService.requireUserId(username);

        MallOrder order = mallOrderService.getById(req.getOrderId());
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("该订单不属于当前用户");
        }
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getCode())) {
            throw new IllegalArgumentException("仅待支付订单可发起支付");
        }

        MallPayment existOne = mallPaymentMapper.selectOne(
                new LambdaQueryWrapper<MallPayment>()
                        .eq(MallPayment::getOrderId, req.getOrderId())
                        .eq(MallPayment::getStatus, PaymentStatusEnum.WAIT_PAY.getCode())
                        .last("limit 1")
        );
        if (existOne != null) {
            return existOne;
        }

        MallPayment mallPayment = new MallPayment();
        mallPayment.setOrderId(order.getId());
        mallPayment.setOrderNo(order.getOrderNo());
        mallPayment.setUserId(order.getUserId());
        mallPayment.setAmount(order.getPayAmount());
        mallPayment.setPayType(req.getPayType());
        mallPayment.setPaymentNo(generatePaymentNo());
        mallPayment.setStatus(PaymentStatusEnum.WAIT_PAY.getCode());

        try {
            mallPaymentMapper.insert(mallPayment);
            return mallPayment;
        } catch (DuplicateKeyException e) {
            return mallPaymentMapper.selectOne(
                    new LambdaQueryWrapper<MallPayment>()
                            .eq(MallPayment::getOrderId, req.getOrderId())
                            .eq(MallPayment::getStatus, PaymentStatusEnum.WAIT_PAY.getCode())
                            .last("limit 1"));
        }
    }

    @Override
    @Transactional
    public MallPayment paymentCallback(PaymentCallbackRequest req) {
        MallPayment mallPayment = mallPaymentMapper.selectOne(
                new LambdaQueryWrapper<MallPayment>()
                        .eq(MallPayment::getPaymentNo, req.getPaymentNo())
                        .last("limit 1")
        );
        if (mallPayment == null) {
            throw new IllegalArgumentException("支付单不存在");
        }
        return doCallback(mallPayment, req);
    }

    @Override
    @Transactional
    public MallPayment paymentCallback(String username, PaymentCallbackRequest req) {
        Long userId = sysUserService.requireUserId(username);
        MallPayment mallPayment = mallPaymentMapper.selectOne(
                new LambdaQueryWrapper<MallPayment>()
                        .eq(MallPayment::getPaymentNo, req.getPaymentNo())
                        .last("limit 1")
        );
        if (mallPayment == null) {
            throw new IllegalArgumentException("支付单不存在");
        }
        if (!mallPayment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("该支付单不属于当前用户");
        }
        return doCallback(mallPayment, req);
    }

    private MallPayment doCallback(MallPayment mallPayment, PaymentCallbackRequest req) {
        if (Objects.equals(mallPayment.getStatus(), PaymentStatusEnum.PAID.getCode())) {
            return mallPayment;
        }
        if (mallPayment.getAmount().compareTo(req.getAmount()) != 0) {
            throw new IllegalArgumentException("支付金额不一致");
        }
        MallOrder mallOrder = mallOrderService.getById(mallPayment.getOrderId());
        if (mallOrder == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!mallOrder.getStatus().equals(OrderStatusEnum.WAIT_PAY.getCode())) {
            throw new IllegalArgumentException("订单状态异常");
        }
        mallPayment.setStatus(PaymentStatusEnum.PAID.getCode());
        mallPayment.setThirdTradeNo(req.getTradeNo());
        Date payTime = new Date();
        mallPayment.setPayTime(payTime);
        mallPayment.setCallbackTime(payTime);
        mallPaymentMapper.updateById(mallPayment);

        mallOrder.setStatus(OrderStatusEnum.PAID.getCode());
        mallOrder.setPayTime(payTime);
        List<MallOrderItem> items =
                mallOrderItemService.list(
                        new LambdaQueryWrapper<MallOrderItem>()
                                .eq(MallOrderItem::getOrderId, mallOrder.getId())
                );
        for (MallOrderItem item : items) {
            mallSkuService.confirmStock(item.getSkuId(), item.getQuantity());
        }
        mallOrderService.updateById(mallOrder);
        return mallPayment;
    }

    private String generatePaymentNo() {
        return "PAY" + IdWorker.getId();
    }
}
