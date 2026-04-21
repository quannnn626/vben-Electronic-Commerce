package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

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
