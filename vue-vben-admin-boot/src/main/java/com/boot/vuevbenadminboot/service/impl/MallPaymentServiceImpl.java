package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallPayment;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.domain.enums.PaymentStatusEnum;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.MallPaymentService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.mapper.MallPaymentMapper;
import com.boot.vuevbenadminboot.web.dto.PaymentCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author quannnn
 * @description 针对表【mall_payment】的数据库操作Service实现
 * @createDate 2026-06-12 14:30:25
 */
@Service
public class MallPaymentServiceImpl extends ServiceImpl<MallPaymentMapper, MallPayment>
        implements MallPaymentService {

    private final SysUserService sysUserService;
    private final MallOrderService mallOrderService;
    private final MallPaymentMapper mallPaymentMapper;

    public MallPaymentServiceImpl(
            SysUserService sysUserService,
            MallOrderService mallOrderService,
            MallPaymentMapper mallPaymentMapper) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallPaymentMapper = mallPaymentMapper;
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

        Long existCount = mallPaymentMapper.selectCount(
                new LambdaQueryWrapper<MallPayment>()
                        .eq(MallPayment::getOrderId, req.getOrderId())
                        .eq(MallPayment::getStatus, PaymentStatusEnum.WAIT_PAY.getCode())
        );
        if (existCount != null && existCount > 0) {
            throw new IllegalArgumentException("该订单已有待支付的支付单，请勿重复操作");
        }

        MallPayment mallPayment = new MallPayment();
        mallPayment.setOrderId(order.getId());
        mallPayment.setOrderNo(order.getOrderNo());
        mallPayment.setUserId(order.getUserId());
        mallPayment.setAmount(order.getPayAmount());
        mallPayment.setPayType(req.getPayType());
        mallPayment.setPaymentNo(generatePaymentNo());
        mallPayment.setStatus(PaymentStatusEnum.WAIT_PAY.getCode());

        int insert = mallPaymentMapper.insert(mallPayment);
        if (insert > 0) {
            return mallPayment;
        } else {
            throw new IllegalArgumentException("支付单创建失败");
        }
    }

    private String generatePaymentNo() {
        return "PAY" + IdWorker.getId();
    }
}
