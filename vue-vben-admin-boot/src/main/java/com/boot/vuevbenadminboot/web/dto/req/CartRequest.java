package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class CartRequest {
    private Long cartId;
    private List<Long> cartIds;
    private Long skuId;
    private Integer quantity;
}
