package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSkuDto {
    private Long fileId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long id;
    private String skuCode;
    private String specName;
    private BigDecimal price;
    private Integer stock;
    private Integer lockedStock;
    private Integer status;
}
