package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.service.MallOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/order")
public class MallOrderController {
    private final MallOrderService orderService;

    public MallOrderController(MallOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            return ApiResponse.of(0, orderService.listOrders(username), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
