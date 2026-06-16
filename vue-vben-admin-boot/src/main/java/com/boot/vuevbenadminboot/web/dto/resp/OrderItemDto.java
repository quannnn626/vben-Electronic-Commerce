package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品项响应出参
 */
@Data
public class OrderItemDto {
    private Long id;
    private Long skuId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
