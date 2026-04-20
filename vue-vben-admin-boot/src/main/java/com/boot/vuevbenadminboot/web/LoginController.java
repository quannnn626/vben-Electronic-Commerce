package com.boot.vuevbenadminboot.web;

import com.boot.vuevbenadminboot.auth.AuthConstants;
import com.boot.vuevbenadminboot.auth.JwtTokenProvider;
import com.boot.vuevbenadminboot.auth.RefreshTokenService;
import com.boot.vuevbenadminboot.auth.SessionActivityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final SessionActivityService sessionActivityService;

    public LoginController(
            JwtTokenProvider jwtTokenProvider,
            RefreshTokenService refreshTokenService,
            SessionActivityService sessionActivityService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.sessionActivityService = sessionActivityService;
    }

    /**
     * 登录接口：
     * accessToken：短时效访问令牌，前端放在 Authorization 里访问业务接口
     * refreshToken：长时效续期令牌，存 Redis 并写入 HttpOnly Cookie；用于 accessToken 过期后换新令牌，减少重复登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(
            @RequestBody Map<String, String> params,
            HttpServletResponse response) {

        String username = params.get("username");
        String password = params.get("password");

        if ("vben".equals(username) && "123456".equals(password)) {
            String accessToken = jwtTokenProvider.createAccessToken(username);
            String refreshToken = refreshTokenService.issueToken(username);
            sessionActivityService.onLogin(username);
            writeRefreshCookie(response, refreshToken);

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", accessToken);
            return buildResponse(0, data, "success");
        }

        return buildResponse(1, null, "用户名或密码错误");
    }

    /**
     * 权限码列表，身份与滑动会话已由 {@link com.boot.vuevbenadminboot.auth.AuthenticationTokenFilter} 校验
     */
    @GetMapping("/codes")
    public Map<String, Object> codes(HttpServletRequest request) {
        String username = (String) request.getAttribute(AuthConstants.REQUEST_USERNAME);
        if (username == null) {
            return buildResponse(-1, null, "未登录");
        }
        List<String> codes = Arrays.asList("AC_100100", "AC_100110");
        return buildResponse(0, codes, "success");
    }

    /**
     * 使用 refreshToken 换新 accessToken
     * 适配当前前端约定：直接返回字符串 token
     */
    @PostMapping("/refresh")
    public String refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readRefreshCookie(request);
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "";
        }
        String username = refreshTokenService.validateAndGetUsername(refreshToken);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "";
        }

        // 轮换 refreshToken，降低泄漏风险
        refreshTokenService.revokeToken(refreshToken);
        String newRefreshToken = refreshTokenService.issueToken(username);
        writeRefreshCookie(response, newRefreshToken);

        sessionActivityService.onLogin(username);
        return jwtTokenProvider.createAccessToken(username);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readRefreshCookie(request);
        if (refreshToken != null) {
            String username = refreshTokenService.validateAndGetUsername(refreshToken);
            refreshTokenService.revokeToken(refreshToken);
            if (username != null) {
                sessionActivityService.invalidate(username);
            }
        }
        clearRefreshCookie(response);
        return buildResponse(0, true, "success");
    }

    private String readRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void writeRefreshCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/api");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/api");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private Map<String, Object> buildResponse(int code, Object data, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("code", code);
        res.put("data", data);
        res.put("message", message);
        return res;
    }
}
