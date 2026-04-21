package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.domain.SysUser;
import com.boot.vuevbenadminboot.service.SysUserService;
import com.boot.vuevbenadminboot.util.PasswordUtil;
import com.boot.vuevbenadminboot.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class RegisterController {
    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/register")
    public Map<String, Object> register(
            @RequestBody RegisterRequest params,
            HttpServletResponse response) {
        String username = params.getUsername();
        String password = params.getPassword();
        String nickname = params.getNickname();
        SysUser sysUser = sysUserService.selectByUsername(username);
        if (sysUser != null) {
            return ApiResponse.of(1, null, "用户名已存在");
        } else {
            sysUser = new SysUser();
        }
        sysUser.setUsername(username);
        sysUser.setPassword(PasswordUtil.encryptPassword(password));
        if (nickname == null || nickname.isBlank()) {
            sysUser.setNickname(username);
        } else {
            sysUser.setNickname(nickname.trim());
        }
        sysUserService.insert(sysUser);
        return ApiResponse.of(0, null, "success");
    }
}
