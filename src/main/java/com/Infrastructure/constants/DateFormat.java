package com.infrastructure.constants;

public enum DateFormat {
    GREGORIAN("yyyy-MM-dd"),
    PERSIAN("yyyy/MM/dd");

    private final String value;

    DateFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
