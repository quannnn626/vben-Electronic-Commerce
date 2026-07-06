package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

@Getter
public enum AfterSaleTypeEnum {
    REFUND_ONLY(0, "仅退款"),
    REFUND_RETURN(1, "退货退款"),
    EXCHANGE(2, "换货");

    private final int code;
    private final String desc;

    AfterSaleTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AfterSaleTypeEnum of(Integer code) {
        if (code == null) return null;
        for (AfterSaleTypeEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
