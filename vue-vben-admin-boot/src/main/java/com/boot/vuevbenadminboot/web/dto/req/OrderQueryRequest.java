package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class OrderQueryRequest {
    private String orderNo;
    private String username;
    private Integer status;
    private Date createTime;
    private Date endCreateTime;
}
