package com.boot.vuevbenadminboot.web;

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
    public Map<String, Object> getUserInfo() {

        Map<String, Object> data = new HashMap<>();
        data.put("roles", Arrays.asList("admin"));
        data.put("realName", "管理员");

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
