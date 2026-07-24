package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.boot.vuevbenadminboot.service.MallAfterSaleService;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleAuditRequest;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleBatchAuditRequest;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleRequest;
import com.boot.vuevbenadminboot.web.dto.resp.AfterSaleAdminListDto;
import com.boot.vuevbenadminboot.web.dto.resp.AfterSaleDetailDto;
import com.boot.vuevbenadminboot.web.dto.resp.AfterSaleResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mall/afterSale")
public class MallAfterSaleController {
    private final MallAfterSaleService mallAfterSaleService;

    public MallAfterSaleController(MallAfterSaleService mallAfterSaleService) {
        this.mallAfterSaleService = mallAfterSaleService;
    }

    @PostMapping("/create")
    public Map<String, Object> createAfterSale(@RequestBody AfterSaleRequest afterSaleRequest,
                                               HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "用户未登录");
            }
            MallAfterSale afterSale = mallAfterSaleService.createAfterSale(afterSaleRequest, username);
            AfterSaleResponse resp = toResponse(afterSale);
            return ApiResponse.of(0, resp, "售后申请成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(-1, null, e.getMessage());
        }
    }

    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam Long id, HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "用户未登录");
        }
        try {
            AfterSaleDetailDto detail = mallAfterSaleService.getAfterSaleDetail(id, username);
            return ApiResponse.of(0, detail, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "用户未登录");
        }
        try {
            List<AfterSaleDetailDto> list = mallAfterSaleService.listAfterSales(username);
            return ApiResponse.of(0, list, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/audit")
    public Map<String, Object> audit(@RequestBody AfterSaleAuditRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            mallAfterSaleService.audit(request, username);
            return ApiResponse.of(0, null, "审核完成");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/audit/batch")
    public Map<String, Object> batchAudit(@RequestBody AfterSaleBatchAuditRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            mallAfterSaleService.batchAudit(request, username);
            return ApiResponse.of(0, null, "批量审核完成");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @GetMapping("/admin/list")
    public Map<String, Object> adminList(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            List<AfterSaleAdminListDto> list = mallAfterSaleService.listAfterSalesAdmin();
            return ApiResponse.of(0, list, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    private AfterSaleResponse toResponse(MallAfterSale entity) {
        AfterSaleResponse resp = new AfterSaleResponse();
        resp.setId(entity.getId());
        resp.setAfterSaleNo(entity.getAfterSaleNo());
        resp.setOrderId(entity.getOrderId());
        resp.setOrderItemId(entity.getOrderItemId());
        resp.setType(entity.getType());
        resp.setStatus(entity.getStatus());
        resp.setReason(entity.getReason());
        resp.setDescription(entity.getDescription());
        resp.setRefundAmount(entity.getRefundAmount());
        resp.setCreateTime(entity.getCreateTime());
        return resp;
    }
}
