package com.boot.vuevbenadminboot.web;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一接口响应构造器
 */
public final class ApiResponse {

    private ApiResponse() {}

    public static Map<String, Object> of(int code, Object data, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }
}

