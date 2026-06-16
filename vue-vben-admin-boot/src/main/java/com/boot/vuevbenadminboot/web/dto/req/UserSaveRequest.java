package com.boot.vuevbenadminboot.web.dto.req;

import lombok.Data;

/**
 * 用户新增/编辑请求入参
 */
@Data
public class UserSaveRequest {
    private Long userId;
    private String introduction;
    private String username;
    private String password;
    private String realName;
    private String roles;
}
