package com.infrastructure.util;

public class NumberUtil {
    public static Long longValue(Object number) {
        if (AppUtils.isNull(number))
            return null;
        else if (number instanceof Number)
            return ((Number) number).longValue();
        else
            try {
                return Long.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }
}
