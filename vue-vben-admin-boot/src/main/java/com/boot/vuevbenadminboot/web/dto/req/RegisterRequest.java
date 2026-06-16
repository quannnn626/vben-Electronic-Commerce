package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 注册请求入参
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nickname;
}
