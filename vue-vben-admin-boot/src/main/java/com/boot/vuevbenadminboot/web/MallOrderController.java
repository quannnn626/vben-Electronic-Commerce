package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.web.dto.req.OrderCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody OrderCreateRequest req, HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            return ApiResponse.of(0, orderService.createOrder(username, req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public Map<String, Object> cancel(@RequestParam Long orderId, HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            orderService.cancelOrder(username, orderId);
            return ApiResponse.of(0, null, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/finish")
    public Map<String, Object> finish(@RequestParam Long orderId, HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            orderService.finishOrder(username, orderId);
            return ApiResponse.of(0, null, "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
