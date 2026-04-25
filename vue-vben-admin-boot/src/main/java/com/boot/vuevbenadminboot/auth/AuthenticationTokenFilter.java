package com.boot.vuevbenadminboot.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 过滤器
 */
@Component
@Order(20)
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SessionActivityService sessionActivityService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationTokenFilter(
            JwtTokenProvider jwtTokenProvider, SessionActivityService sessionActivityService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.sessionActivityService = sessionActivityService;
    }

    //每个请求都会经过这个方法
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 跨域直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 登录、注册、刷新、登出、静态附件接口直接放行
        String path = request.getServletPath();
        if ("/auth/login".equals(path)
                || "/auth/register".equals(path)
                || "/auth/refresh".equals(path)
                || "/auth/logout".equals(path)
                || path.startsWith("/upload/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 其余路径校验令牌，取Header里的Bearer令牌
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            sendJsonUnauthorized(request, response, "未提供访问令牌");
            return;
        }

        // 抽出纯token，去掉Bearer前缀，格式为Authorization: Bearer eyJhbGciOiJI...
        String token = auth.substring(7).trim();
        Claims claims;
        //解析JWT令牌，校验token是否有效，是否过期，是否被篡改
        try {
            claims = jwtTokenProvider.parseValid(token);
        } catch (JwtException e) {
            sendJsonUnauthorized(request, response, "令牌无效或已过期");
            return;
        }

        // 从 token 中提取用户名，并记录活跃时间（当前不基于空闲时间强制下线）
        String username = claims.getSubject();
        // if (!sessionActivityService.validateAndTouch(username)) {
        //     sendJsonUnauthorized(response, "长时间未操作或会话已失效，请重新登录");
        //     return;
        // }

        request.setAttribute(AuthConstants.REQUEST_USERNAME, username);
        filterChain.doFilter(request, response);
    }

    // 发送 JSON 401 响应，包含错误信息
    private void sendJsonUnauthorized(
            HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        // 过滤器里提前返回 401 时，需要手动补 CORS 头，否则浏览器会报跨域而不是让前端拿到 401
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isBlank()) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Vary", "Origin");
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Map<String, Object> body = new HashMap<>();
        body.put("code", 401);
        body.put("data", null);
        body.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
