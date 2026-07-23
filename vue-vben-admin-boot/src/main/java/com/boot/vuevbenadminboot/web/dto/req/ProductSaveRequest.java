package com.boot.vuevbenadminboot.web.dto.req;

import com.boot.vuevbenadminboot.web.dto.ProductSkuDto;
import lombok.Data;

import java.util.List;

/**
 * 商品新增/编辑请求入参
 */
@Data
public class ProductSaveRequest {
    private List<Long> categoryIds;
    private String description;
    private Long id;
    private String name;
    private List<ProductSkuDto> skus;
    private Integer status;
}
