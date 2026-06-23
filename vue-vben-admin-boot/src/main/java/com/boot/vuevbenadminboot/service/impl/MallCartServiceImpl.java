package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallCart;
import com.boot.vuevbenadminboot.domain.MallSku;
import com.boot.vuevbenadminboot.domain.enums.CommonStatusEnum;
import com.boot.vuevbenadminboot.service.MallCartService;
import com.boot.vuevbenadminboot.mapper.MallCartMapper;
import com.boot.vuevbenadminboot.service.MallSkuService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.util.QuantityUtil;
import com.boot.vuevbenadminboot.web.dto.req.CartRequest;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author quannnn
 * @description 针对表【mall_cart(购物车表)】的数据库操作Service实现
 * @createDate 2026-06-23 13:48:57
 */
@Service
public class MallCartServiceImpl extends ServiceImpl<MallCartMapper, MallCart>
        implements MallCartService {
    private final SysUserService sysUserService;
    private final MallSkuService mallSkuService;

    public MallCartServiceImpl(SysUserService sysUserService,
                               MallSkuService mallSkuService) {
        this.sysUserService = sysUserService;
        this.mallSkuService = mallSkuService;
    }

    @Override
    @Transactional
    public MallCart createCart(String username, CartRequest req) {
        Long userId = sysUserService.requireUserId(username);
        int qty = QuantityUtil.requirePositive(req.getQuantity());
        MallSku sku = mallSkuService.getById(req.getSkuId());
        if (sku == null) {
            throw new IllegalArgumentException("该型号不存在");
        }
        if (!Objects.equals(CommonStatusEnum.ENABLED.getCode(), sku.getStatus())) {
            throw new IllegalArgumentException("商品已下架");
        }
        if (sku.getStock() == null || sku.getStock() <= 0) {
            throw new IllegalArgumentException("商品库存不足");
        }
        MallCart existCart = this.getOne(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getUserId, userId)
                .eq(MallCart::getSkuId, req.getSkuId())
        );
        if (existCart != null) {
            int newQty = existCart.getQuantity() + qty;
            if (sku.getStock() < newQty) {
                throw new IllegalArgumentException("库存不足，无法加入购物车");
            }
            existCart.setQuantity(newQty);
            this.updateById(existCart);
            return existCart;
        }
        if (sku.getStock() < qty) {
            throw new IllegalArgumentException("库存不足，无法加入购物车");
        }
        MallCart cart = new MallCart();
        cart.setUserId(userId);
        cart.setProductId(sku.getProductId());
        cart.setSkuId(req.getSkuId());
        cart.setQuantity(qty);
        this.save(cart);
        return cart;
    }
}




