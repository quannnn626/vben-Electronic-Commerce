package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallCart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.req.CartRequest;
import com.boot.vuevbenadminboot.web.dto.resp.CartItemDto;

import java.util.List;

/**
 * @author quannnn
 * @description 针对表【mall_cart(购物车表)】的数据库操作Service
 * @createDate 2026-06-23 13:48:57
 */
public interface MallCartService extends IService<MallCart> {
    MallCart createCart(String username, CartRequest req);

    MallCart updateCart(String username, CartRequest req);

    List<CartItemDto> listCart(String username);
}
