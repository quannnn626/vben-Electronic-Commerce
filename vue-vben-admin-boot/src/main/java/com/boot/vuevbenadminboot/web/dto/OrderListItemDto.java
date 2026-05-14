package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderListItemDto {
    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
    private Date createTime;
    private Date payTime;
    private Date deliveryTime;
    private Date finishTime;
    private List<OrderItemDto> items;
}
