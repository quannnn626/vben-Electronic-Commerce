package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    /**
     * 购物车ID
     */
    private Long cartId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图
     */
    private String productImage;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * SKU规格描述
     */
    private String specText;

    /**
     * 销售价
     */
    private BigDecimal salePrice;

    /**
     * 当前库存
     */
    private Integer stock;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 小计金额
     */
    private BigDecimal subtotalAmount;
}
