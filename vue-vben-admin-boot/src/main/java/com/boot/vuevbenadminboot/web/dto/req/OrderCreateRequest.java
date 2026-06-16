package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求入参
 */
@Data
public class OrderCreateRequest {
    private Long addressId;
    private List<OrderItemRequest> items;
    private String paymentMethod;
    private String remark;

    @Data
    public static class OrderItemRequest {
        private Long skuId;
        private String productName;
        private String productImage;
        private String skuSpecName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
