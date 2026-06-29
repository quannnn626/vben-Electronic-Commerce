package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.service.MallOrderService;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.web.dto.req.OrderCreateRequest;
import com.boot.vuevbenadminboot.web.dto.req.OrderQueryRequest;
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
    private final SysUserService sysUserService;

    public MallOrderController(MallOrderService orderService, SysUserService sysUserService) {
        this.orderService = orderService;
        this.sysUserService = sysUserService;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestParam(required = false) Integer status, HttpServletRequest request) {
        String username = getLoginUsername(request);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            return ApiResponse.of(0, orderService.listOrders(username, status), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody OrderCreateRequest req, HttpServletRequest request) {
        String username = getLoginUsername(request);
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
        String username = getLoginUsername(request);
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
        String username = getLoginUsername(request);
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

    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam Long orderId, HttpServletRequest request) {
        String username = getLoginUsername(request);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        try {
            return ApiResponse.of(0, orderService.getOrderDetail(username, orderId), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PostMapping("/admin/list")
    public Map<String, Object> getAllUserList(@RequestBody OrderQueryRequest req, HttpServletRequest request) {
        String username = getLoginUsername(request);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        SysUser user = sysUserService.selectByUsername(username);
        if (user == null || !"super".equals(user.getRole())) {
            return ApiResponse.of(-1, null, "无权限");
        }
        try {
            return ApiResponse.of(0, orderService.getAllUserList(req), "success");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    private String getLoginUsername(HttpServletRequest request) {
        return (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
    }
}
