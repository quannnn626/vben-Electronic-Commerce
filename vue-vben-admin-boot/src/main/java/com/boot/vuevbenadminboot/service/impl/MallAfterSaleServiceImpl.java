package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.domain.MallOrderItem;
import com.boot.vuevbenadminboot.domain.enums.AfterSaleStatusEnum;
import com.boot.vuevbenadminboot.domain.enums.OrderStatusEnum;
import com.boot.vuevbenadminboot.mapper.MallOrderItemMapper;
import com.boot.vuevbenadminboot.service.MallAfterSaleService;
import com.boot.vuevbenadminboot.mapper.MallAfterSaleMapper;
import com.boot.vuevbenadminboot.service.MallOrderItemService;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.MallResourceRelService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    public MallAfterSaleServiceImpl(SysUserService sysUserService,
                                    MallOrderService mallOrderService,
                                    MallOrderItemService mallOrderItemService,
                                    MallOrderItemMapper mallOrderItemMapper,
                                    MallAfterSaleMapper mallAfterSaleMapper,
                                    MallResourceRelService resourceRelService) {
        this.sysUserService = sysUserService;
        this.mallOrderService = mallOrderService;
        this.mallOrderItemService = mallOrderItemService;
        this.mallOrderItemMapper = mallOrderItemMapper;
        this.mallAfterSaleMapper = mallAfterSaleMapper;
        this.resourceRelService = resourceRelService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallAfterSale createAfterSale(AfterSaleRequest request, String userName) {
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
        MallOrderItem mallOrderItem = mallOrderItemMapper.selectOne(
                new LambdaQueryWrapper<MallOrderItem>()
                        .eq(MallOrderItem::getOrderId, request.getOrderId())
                        .eq(MallOrderItem::getSkuId, request.getOrderItemId())
        );
        if (mallOrderItem == null) {
            throw new IllegalArgumentException("该商品不存在");
        }
        Long count = mallAfterSaleMapper.selectCount(
                new LambdaQueryWrapper<MallAfterSale>()
                        .eq(MallAfterSale::getOrderItemId, request.getOrderItemId())
                        .in(MallAfterSale::getStatus,
                                AfterSaleStatusEnum.APPLYING.getCode(),
                                AfterSaleStatusEnum.APPROVING.getCode(),
                                AfterSaleStatusEnum.APPROVED.getCode(),
                                AfterSaleStatusEnum.REFUNDED.getCode()
                        )
        );
        if (count > 0) {
            throw new IllegalArgumentException("该商品已申请售后，不能重复申请");
        }
        MallAfterSale afterSale = new MallAfterSale();
        afterSale.setOrderId(request.getOrderId());
        afterSale.setOrderItemId(request.getOrderItemId());
        afterSale.setUserId(userId);
        afterSale.setType(request.getType());
        afterSale.setReason(request.getReason());
        afterSale.setDescription(request.getDescription());
        afterSale.setStatus(AfterSaleStatusEnum.APPLYING.getCode());
        afterSale.setCreateTime(new Date());
        afterSale.setUpdateTime(new Date());
        afterSale.setDeleted(0);
        this.save(afterSale);

        // 写入凭证附件关联
        List<Long> fileIds = request.getFileIds();
        if (fileIds != null && !fileIds.isEmpty()) {
            resourceRelService.attachBatch("after_sale", afterSale.getId(), fileIds, "proof");
        }
        return afterSale;
    }
}




