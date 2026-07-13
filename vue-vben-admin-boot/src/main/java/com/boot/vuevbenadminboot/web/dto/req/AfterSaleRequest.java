package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AfterSaleRequest {
    private Long orderId;
    private Long orderItemId;
    private Integer type;
    private Integer reason;
    private String description;
    private Integer quantity;
    private List<AfterSaleItemRequest> items;
    private BigDecimal refundAmount;
    private List<Long> fileIds;
}
