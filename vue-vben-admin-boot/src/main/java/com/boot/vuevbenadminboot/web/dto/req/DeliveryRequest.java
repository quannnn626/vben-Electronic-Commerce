package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

@Data
public class DeliveryRequest {
    private String orderNo;
    private String logisticsCompany;
    private String trackingNo;
    private String message;
}
