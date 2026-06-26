package com.boot.vuevbenadminboot.web.dto.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单列表/详情响应出参
 */
@Data
public class OrderListItemDto {
    private Long id;
    private String orderNo;
    private Long userId;
    private String username;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Date createTime;
    private Date payTime;
    private Date deliveryTime;
    private Date finishTime;
    private Date cancelTime;
    private List<OrderItemDto> items;
}
