package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class DeliveryRequest {
    private String orderNo;
    private List<DeliveryItemDto> items;
    private String logisticsCompany;
    private String trackingNo;
    private String message;
}
