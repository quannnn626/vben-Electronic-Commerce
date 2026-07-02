package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SKU规格DTO 入参/出参
 */
@Data
public class ProductSkuDto {
    private Long fileId;
    private List<Long> fileIds;
    private List<ProductSkuFileDto> extraFiles;
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
