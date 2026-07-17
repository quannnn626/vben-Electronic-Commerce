package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AfterSaleAdminListDto {

    private Long id;

    private String afterSaleNo;

    private String orderNo;

    private String username;

    private Integer type;

    private Integer status;

    private BigDecimal refundAmount;

    private Date createTime;

    private List<OrderItemDto> items;
}
