package com.boot.vuevbenadminboot.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    // 与 login 返回的 accessToken 保持一致
    private static final String DEMO_ACCESS_TOKEN = "mock-token-123";

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {

        String username = params.get("username");
        String password = params.get("password");

        // 先写死以实现登录功能
        if ("vben".equals(username) && "123456".equals(password)) {

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", DEMO_ACCESS_TOKEN);

            return buildResponse(0, data, "success");
        }

        return buildResponse(1, null, "用户名或密码错误");
    }

    /**
     * 权限码列表：用于按钮级、接口级等细粒度权限
     * 前端登录成功后会带上 Authorization 请求本接口；不需要细粒度权限时可返回空列表。
     */
    @GetMapping("/codes")
    public Map<String, Object> codes(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return buildResponse(-1, null, "未登录");
        }
        String token = auth.substring(7).trim();
        if (!DEMO_ACCESS_TOKEN.equals(token)) {
            return buildResponse(-1, null, "令牌无效");
        }
        List<String> codes = Arrays.asList("AC_100100", "AC_100110");
        return buildResponse(0, codes, "success");
    }

    private Map<String, Object> buildResponse(int code, Object data, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }
}
