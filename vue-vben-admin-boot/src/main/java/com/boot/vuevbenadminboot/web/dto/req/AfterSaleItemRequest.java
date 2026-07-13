package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

@Data
public class AfterSaleItemRequest {
    private Long orderItemId;
    private Integer quantity;
}
