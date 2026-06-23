package com.boot.vuevbenadminboot.util;

public final class QuantityUtil {
    private QuantityUtil() {}

    // 校验数量
    public static int requirePositive(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }
        return quantity;
    }
}
