package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.service.MallUserAddressService;
import com.boot.vuevbenadminboot.web.dto.UserAddressSaveRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mall/address")
public class MallUserAddressController {
    private final MallUserAddressService addressService;

    public MallUserAddressController(MallUserAddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public Map<String, Object> list(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }
        return ApiResponse.of(0, addressService.listByUsername(username), "success");
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody UserAddressSaveRequest req, HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "未登录");
            }
            Long id = addressService.createAddress(username, req);
            return ApiResponse.of(0, id, "新增地址成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody UserAddressSaveRequest req, HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "未登录");
            }
            boolean ok = addressService.updateAddress(username, req);
            return ApiResponse.of(0, ok, "更新地址成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestParam Long id, HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
            if (username == null) {
                return ApiResponse.of(-1, null, "未登录");
            }
            boolean ok = addressService.deleteAddress(username, id);
            return ApiResponse.of(0, ok, "删除地址成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.of(1, null, e.getMessage());
        }
    }
}