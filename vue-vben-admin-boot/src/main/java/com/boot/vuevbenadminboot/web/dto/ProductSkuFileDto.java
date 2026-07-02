package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

/**
 * SKU 附件信息（出参用）
 */
@Data
public class ProductSkuFileDto {
    private Long id;
    private String fileName;
    private String filePath;
    private String fileType;
}
