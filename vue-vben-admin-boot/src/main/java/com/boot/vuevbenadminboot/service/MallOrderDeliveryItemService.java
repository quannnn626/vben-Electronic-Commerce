package com.boot.vuevbenadminboot.service;

import com.boot.vuevbenadminboot.domain.MallOrderDeliveryItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author quannnn
* @description 针对表【mall_order_delivery_item(物流商品关联表)】的数据库操作Service
* @createDate 2026-07-28 15:52:08
*/
import java.util.List;

public interface MallOrderDeliveryItemService extends IService<MallOrderDeliveryItem> {

    List<Long> getDeliveredItemIds(Long deliveryId);
}
