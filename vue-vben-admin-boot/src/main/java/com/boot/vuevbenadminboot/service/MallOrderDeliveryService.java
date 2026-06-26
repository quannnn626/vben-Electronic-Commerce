package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallOrderDelivery;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.req.DeliveryRequest;

import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_order_delivery】的数据库操作Service
* @createDate 2026-06-26 14:11:24
*/
public interface MallOrderDeliveryService extends IService<MallOrderDelivery> {
    MallOrderDelivery createDelivery(DeliveryRequest req,String username);
}
