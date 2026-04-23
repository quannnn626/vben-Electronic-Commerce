package com.boot.vuevbenadminboot.service.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StockManageItemDto {
    private Integer availableStock;
    private Integer lockedStock;
    private Long productId;
    private String productName;
    private Integer totalStock;
    private Date updateTime;
}
