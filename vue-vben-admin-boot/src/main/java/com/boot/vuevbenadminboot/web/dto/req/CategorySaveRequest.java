package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 商品分类新增/编辑请求入参
 */
@Data
public class CategorySaveRequest {
    private Long id;
    private String code;
    private Integer level;
    private String name;
    private Long parentId;
    private String remark;
    private Integer sort;
    private Boolean status;
}
