package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.MallAfterSale;
import com.boot.vuevbenadminboot.service.MallAfterSaleService;
import com.boot.vuevbenadminboot.web.dto.req.AfterSaleRequest;
import com.boot.vuevbenadminboot.web.dto.resp.AfterSaleResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
