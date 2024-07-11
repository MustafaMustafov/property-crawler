package com.property.crawler.enums;

public enum ActionType {
    UNKNOWN(0, ""),
    PRODAJBA(1, "Продажба"),
    POD_NAEM(2, "Наем");

    private int id;

    private String value;

    ActionType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static String getValueById(int id) {
        for (ActionType e : values()) {
            if (e.id == id) {
                return e.value;
            }
        }
        return UNKNOWN.value;
    }
}
