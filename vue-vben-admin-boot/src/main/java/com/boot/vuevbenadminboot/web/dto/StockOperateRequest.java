package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

@Data
public class StockOperateRequest {
    private Long skuId;
    private Integer quantity;
}
