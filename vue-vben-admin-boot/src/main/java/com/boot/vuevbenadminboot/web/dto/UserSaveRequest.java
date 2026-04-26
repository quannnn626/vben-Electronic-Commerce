package com.boot.vuevbenadminboot.web.dto;

import lombok.Data;

@Data
public class UserSaveRequest {
    private Long userId;
    private String introduction;
    private String username;
    private String password;
    private String realName;
    private String roles;
}
