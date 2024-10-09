package com.property.crawler.enums;

public enum ConstructionType {
    UNKNOWN("Няма информация"),
    PANEL("Панел"),
    TUHLA("Тухла");

    public String getValue() {
        return value;
    }

    private String value;

    ConstructionType(String value) {
        this.value = value;
    }

    public static ConstructionType getConstructionTypeByValue(String constructionType) {
        for (ConstructionType e : values()) {
            if (e.value.contains(constructionType)) {
                return e;
            }
        }
        return UNKNOWN;
    }

    public static String getConstructionValueByType(String constructionType) {
        for (ConstructionType e: values()) {
            if (e.value.equalsIgnoreCase(constructionType)){
                return e.value;
            }
        }
        return UNKNOWN.value;
    }
}
