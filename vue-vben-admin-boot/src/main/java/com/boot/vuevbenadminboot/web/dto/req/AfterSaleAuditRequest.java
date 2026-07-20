package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

@Data
public class AfterSaleAuditRequest {
    private Long id;
    private Integer status;
    private String auditRemark;
}
