package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

/**
 * 售后状态枚举类
 * @author quannnn
 */
@Getter
public enum AfterSaleStatusEnum {
    APPLYING(0, "申请中"),
    APPROVING(1, "审核中"),
    APPROVED(2, "已通过"),
    REJECTED(3, "已拒绝"),
    REFUNDED(4, "退款中"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消");

    private final int code;
    private final String desc;

    AfterSaleStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AfterSaleStatusEnum of(Integer code) {
        if (code == null) return null;
        for (AfterSaleStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
