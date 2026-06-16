package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 登录请求入参
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
