package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductSaveRequest {
    private List<Long> categoryIds;
    private String description;
    private Long id;
    private String name;
    private List<ProductSkuDto> skus;
    private Integer status;
}
