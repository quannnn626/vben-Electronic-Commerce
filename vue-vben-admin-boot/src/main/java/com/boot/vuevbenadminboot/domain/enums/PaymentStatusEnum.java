package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 * @author quannnn
 */
@Getter
public enum PaymentStatusEnum {

    WAIT_PAY(0, "待支付"),

    PAID(1, "已支付"),

    CLOSED(2, "已关闭");

    private final int code;
    private final String desc;

    PaymentStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举，未匹配返回 null
    public static PaymentStatusEnum of(Integer code) {
        if (code == null) return null;
        for (PaymentStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
