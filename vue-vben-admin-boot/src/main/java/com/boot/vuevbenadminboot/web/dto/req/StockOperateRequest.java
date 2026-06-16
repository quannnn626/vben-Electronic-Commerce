package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 库存操作请求入参
 */
@Data
public class StockOperateRequest {
    private Long skuId;
    private Integer quantity;
}
