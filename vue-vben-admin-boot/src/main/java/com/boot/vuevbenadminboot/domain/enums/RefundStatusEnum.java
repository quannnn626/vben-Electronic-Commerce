package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

@Getter
public enum RefundStatusEnum {
    PENDING(0, "待退款"),
    PROCESSING(1, "退款中"),
    SUCCESS(2, "已退款"),
    FAILED(3, "退款失败");

    private final int code;
    private final String desc;

    RefundStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RefundStatusEnum of(Integer code) {
        if (code == null) return null;
        for (RefundStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
