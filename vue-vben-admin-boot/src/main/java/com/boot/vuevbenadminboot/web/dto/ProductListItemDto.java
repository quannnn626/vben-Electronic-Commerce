package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductListItemDto {
    private List<Long> categoryIds;
    private String description;
    private List<ProductFileDto> files;
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer status;
    private Integer stock;
    private Date createTime;
}
