package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return buildResponse(-1, null, "未登录");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", "1");
        data.put("username", username);
        data.put("realName", "管理员");
        data.put("avatar", "");
        data.put("roles", Arrays.asList("admin"));
        data.put("desc", "");
        data.put("homePath", "/analytics");
        data.put("token", "");
        return buildResponse(0, data, "success");
    }

    private Map<String, Object> buildResponse(int code, Object data, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }
}
