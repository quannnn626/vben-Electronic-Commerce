package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

@Data
public class StockOperateRequest {
    private Long productId;
    private Integer quantity;
}
