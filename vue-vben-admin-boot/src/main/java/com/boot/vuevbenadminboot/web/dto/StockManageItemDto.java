package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.util.Date;

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
