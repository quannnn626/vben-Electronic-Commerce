package com.boot.vuevbenadminboot.domain.enums;

import lombok.Getter;

@Getter
public enum AfterSaleReasonEnum {
    QUALITY(0, "质量问题"),
    NOT_AS_DESCRIBED(1, "商品与描述不符"),
    WRONG_ITEM(2, "发错货"),
    DAMAGED(3, "商品破损"),
    SIZE_ISSUE(4, "尺码/规格不合适"),
    DONT_WANT(5, "不想要了"),
    OTHER(6, "其他");

    private final int code;
    private final String desc;

    AfterSaleReasonEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AfterSaleReasonEnum of(Integer code) {
        if (code == null) return null;
        for (AfterSaleReasonEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
