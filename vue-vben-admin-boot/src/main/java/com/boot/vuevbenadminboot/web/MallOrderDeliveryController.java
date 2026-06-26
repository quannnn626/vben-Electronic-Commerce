package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.MallOrderDelivery;
import com.boot.vuevbenadminboot.service.MallOrderDeliveryService;
import com.boot.vuevbenadminboot.web.dto.req.DeliveryRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/order/delivery")
public class MallOrderDeliveryController {
    private final MallOrderDeliveryService mallOrderDeliveryService;

    public MallOrderDeliveryController(MallOrderDeliveryService mallOrderDeliveryService) {
        this.mallOrderDeliveryService = mallOrderDeliveryService;
    }

    @PostMapping("/create")
    public Map<String, Object> createDelivery(@RequestBody DeliveryRequest req, HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            MallOrderDelivery delivery = mallOrderDeliveryService.createDelivery(req, username);
            return ApiResponse.of(0, delivery, "发货成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
