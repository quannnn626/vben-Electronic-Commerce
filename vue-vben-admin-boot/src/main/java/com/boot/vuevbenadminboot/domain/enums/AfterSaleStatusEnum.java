package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

/**
 * 售后状态枚举类
 * @author quannnn
 */
@Getter
public enum AfterSaleStatusEnum {
    APPLYING(0, "申请中"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    REFUNDING(3, "退款中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    WAIT_RETURN(6, "待退货");

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
