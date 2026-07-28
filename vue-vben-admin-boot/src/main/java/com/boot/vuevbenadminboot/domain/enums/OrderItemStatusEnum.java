package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

@Getter
public enum OrderItemStatusEnum {
    WAIT_DELIVERY(0, "待发货"),
    SHIPPED(1, "已发货"),
    IN_TRANSIT(2, "运输中"),
    RECEIVED(3, "已收货"),
    COMPLETED(4, "已完成"),
    AFTER_SALE(5, "售后中");

    private final int code;
    private final String desc;

    OrderItemStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderItemStatusEnum of(Integer code) {
        if (code == null) return null;
        for (OrderItemStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
