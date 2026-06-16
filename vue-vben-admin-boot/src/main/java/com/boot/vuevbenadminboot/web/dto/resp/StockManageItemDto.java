package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.util.Date;

/**
 * 库存管理列表项响应出参
 */
@Data
public class StockManageItemDto {
    private Integer availableStock;
    private Integer lockedStock;
    private Long productId;
    private String productName;
    private String skuCode;
    private Long skuId;
    private String specName;
    private Integer totalStock;
    private Date updateTime;
}
