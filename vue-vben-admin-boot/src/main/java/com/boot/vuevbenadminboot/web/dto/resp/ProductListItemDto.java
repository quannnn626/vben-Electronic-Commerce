package com.boot.vuevbenadminboot.web.dto.resp;

import com.boot.vuevbenadminboot.web.dto.ProductSkuDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品列表项响应出参
 */
@Data
public class ProductListItemDto {
    private List<Long> categoryIds;
    private List<String> categoryNames;
    private String description;
    private Long id;
    private String name;
    private BigDecimal price;
    private List<ProductSkuDto> skus;
    private Integer status;
    private Integer stock;
    private Date createTime;
}
