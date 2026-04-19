package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrder;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.mapper.MallOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author quannnn
* @description 针对表【mall_order(订单表)】的数据库操作Service实现
* @createDate 2026-04-19 19:20:12
*/
@Service
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder>
    implements MallOrderService{

}




