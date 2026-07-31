package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 * @author quannnn
 */
@Getter
public enum OrderStatusEnum {

    WAIT_PAY(0, "待支付"),

    PAID(1, "已支付"),

    CANCELLED(2, "已取消"),

    CLOSED(3, "已关闭"),

    SHIPPED(4, "已发货"),

    COMPLETED(5, "已完成");

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据 code 获取枚举，未匹配返回 null
    public static OrderStatusEnum of(Integer code) {
        if (code == null) return null;
        for (OrderStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
