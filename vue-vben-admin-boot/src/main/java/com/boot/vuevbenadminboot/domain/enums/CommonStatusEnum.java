package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

/**
 * 通用启用/禁用状态枚举
 * @author quannnn
 */
@Getter
public enum CommonStatusEnum {

    // 禁用/下架
    DISABLED(0, "禁用"),

    // 启用/上架
    ENABLED(1, "启用");

    private final int code;
    private final String desc;

    CommonStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举，未匹配返回 null
    public static CommonStatusEnum of(Integer code) {
        if (code == null) return null;
        for (CommonStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }

    /** Boolean → 枚举：true=ENABLED, false=DISABLED */
    public static CommonStatusEnum fromBoolean(Boolean b) {
        return Boolean.TRUE.equals(b) ? ENABLED : DISABLED;
    }

    /** 枚举 → Boolean */
    public boolean toBoolean() {
        return this == ENABLED;
    }
}
