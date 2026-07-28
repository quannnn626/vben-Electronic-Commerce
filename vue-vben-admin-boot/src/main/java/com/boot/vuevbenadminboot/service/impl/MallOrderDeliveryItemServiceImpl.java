package com.boot.vuevbenadminboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.boot.vuevbenadminboot.domain.MallOrderDeliveryItem;
import com.boot.vuevbenadminboot.service.MallOrderDeliveryItemService;
import com.boot.vuevbenadminboot.mapper.MallOrderDeliveryItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallOrderDeliveryItemServiceImpl extends ServiceImpl<MallOrderDeliveryItemMapper, MallOrderDeliveryItem>
    implements MallOrderDeliveryItemService{

    @Override
    public List<Long> getDeliveredItemIds(List<Long> deliveryIds) {
        if (deliveryIds.isEmpty()) return List.of();
        return this.list(new LambdaQueryWrapper<MallOrderDeliveryItem>()
                        .in(MallOrderDeliveryItem::getDeliveryId, deliveryIds))
                .stream()
                .map(MallOrderDeliveryItem::getOrderItemId)
                .toList();
    }
}




