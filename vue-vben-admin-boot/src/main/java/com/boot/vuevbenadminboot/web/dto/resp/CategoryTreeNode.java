package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 商品分类树节点响应出参
 */
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
