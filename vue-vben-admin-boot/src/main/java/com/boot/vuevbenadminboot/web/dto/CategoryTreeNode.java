package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CategoryTreeNode {
    private List<CategoryTreeNode> children;
    private String code;
    private Date createTime;
    private Long id;
    private Integer level;
    private String name;
    private Long parentId;
    private String remark;
    private Integer sort;
    private Boolean status;
}
