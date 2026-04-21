package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/info")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return ApiResponse.of(-1, null, "未登录");
        }

        Map<String, Object> data = new HashMap<>();
        SysUser sysUser = sysUserService.selectByUsername(username);
        data.put("userId", sysUser.getId());
        data.put("username", sysUser.getUsername());
        data.put("realName", sysUser.getNickname());
        data.put("avatar", sysUser.getAvatar() == null ? "" : sysUser.getAvatar());
        data.put("roles", Arrays.asList(sysUser.getRole()));
        data.put("desc", sysUser.getUserDesc() == null ? "" : sysUser.getUserDesc());
        data.put("homePath", sysUser.getHomePath() == null ? "/analytics" : sysUser.getHomePath());
        return ApiResponse.of(0, data, "success");
    }
}
