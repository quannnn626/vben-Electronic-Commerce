package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSaveRequest {
    private List<Long> categoryIds;
    private String description;
    private List<Long> fileIds;
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer status;
    private Integer stock;
}
