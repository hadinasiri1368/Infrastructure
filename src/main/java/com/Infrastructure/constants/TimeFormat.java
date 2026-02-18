package com.infrastructure.constants;

public enum TimeFormat {
    HOUR_MINUTE_SECOND("HH:mm:ss"),
    HOUR_MINUTE("HH:mm"),
    HOUR_MINUTE_SECOND_MILLISECONDS("HH:mm:ss.SSS");

    private final String value;

    TimeFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
