package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.MallCart;
import com.boot.vuevbenadminboot.service.MallCartService;
import com.boot.vuevbenadminboot.web.dto.req.CartRequest;
import com.boot.vuevbenadminboot.web.dto.resp.CartItemDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mall/cart")
public class MallCartController {
    private final MallCartService mallCartService;

    public MallCartController(MallCartService mallCartService) {
        this.mallCartService = mallCartService;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody CartRequest req,
                                      HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "未登录");
            }
            MallCart mallCart = mallCartService.createCart(username, req);
            return ApiResponse.of(0, mallCart, "加入购物车成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "未登录");
            }
            List<CartItemDto> cartItemDtos = mallCartService.listCart(username);
            return ApiResponse.of(0, cartItemDtos, "进入购物车页面");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}
