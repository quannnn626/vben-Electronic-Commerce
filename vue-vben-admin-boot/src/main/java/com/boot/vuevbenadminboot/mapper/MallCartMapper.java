package com.boot.vuevbenadminboot.mapper;

import com.boot.vuevbenadminboot.domain.MallCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boot.vuevbenadminboot.web.dto.resp.CartItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_cart(购物车表)】的数据库操作Mapper
* @createDate 2026-06-23 13:48:57
* @Entity com.boot.vuevbenadminboot.domain.MallCart
*/
@Mapper
public interface MallCartMapper extends BaseMapper<MallCart> {
    List<CartItemDto> listCartByUserId(@Param("userId") Long userId);
}




