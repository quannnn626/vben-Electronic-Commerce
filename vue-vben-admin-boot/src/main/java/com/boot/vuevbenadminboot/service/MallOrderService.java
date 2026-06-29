package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.boot.vuevbenadminboot.web.dto.req.OrderCreateRequest;
import com.boot.vuevbenadminboot.web.dto.req.OrderQueryRequest;
import com.boot.vuevbenadminboot.web.dto.resp.OrderListItemDto;

import java.util.List;

/**
* @author quannnn
* @description 针对表【mall_order(订单表)】的数据库操作Service
* @createDate 2026-04-23 13:48:03
*/
public interface MallOrderService extends IService<MallOrder> {
    List<OrderListItemDto> listOrders(String username, Integer status);

    OrderListItemDto createOrder(String username, OrderCreateRequest req);

    void cancelOrder(String username, Long orderId);

    void finishOrder(String username, Long orderId);

    OrderListItemDto getOrderDetail(String username, Long orderId);

    List<OrderListItemDto> getAllUserList(OrderQueryRequest req);
}
