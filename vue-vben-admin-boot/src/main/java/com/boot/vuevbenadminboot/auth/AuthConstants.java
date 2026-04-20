package com.boot.vuevbenadminboot.auth;

/**
 * 认证模块通用常量
 * 主要用于在过滤器与 Controller 之间传递“当前已认证用户”的请求属性 key
 */
public final class AuthConstants {

    /** 已登录用户名，供 Controller 读取 */
    public static final String REQUEST_USERNAME = "AUTH_USERNAME";

    private AuthConstants() {}
}
