package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class AfterSaleBatchAuditRequest {
    private List<AuditItem> items;

    @Data
    public static class AuditItem {
        private Long id;
        private Integer status;
        private String auditRemark;
    }
}
