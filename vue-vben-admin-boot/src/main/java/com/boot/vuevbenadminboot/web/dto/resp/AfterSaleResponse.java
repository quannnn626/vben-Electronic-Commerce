package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AfterSaleResponse {
    private Long id;
    private String afterSaleNo;
    private Long orderId;
    private Long orderItemId;
    private Integer type;
    private Integer status;
    private String reason;
    private String description;
    private BigDecimal refundAmount;
    private Date createTime;
}
