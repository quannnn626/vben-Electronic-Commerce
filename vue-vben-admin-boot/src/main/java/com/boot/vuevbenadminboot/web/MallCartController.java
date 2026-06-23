package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.MallCart;
import com.boot.vuevbenadminboot.service.MallCartService;
import com.boot.vuevbenadminboot.web.dto.req.CartRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
