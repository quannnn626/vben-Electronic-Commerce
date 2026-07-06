package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AfterSaleDetailDto {

    // 售后信息
    private Long id;
    private String afterSaleNo;
    private Integer type;
    private Integer reason;
    private String reasonDesc;
    private Integer status;
    private BigDecimal refundAmount;
    private Date applyTime;
    private Date auditTime;
    private Date finishTime;

    // 订单信息
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal payAmount;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    // 商品
    private List<OrderItemDto> items;
}
