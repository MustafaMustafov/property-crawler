package com.property.crawler.enums;

public enum ConstructionType {
    UNKNOWN("Няма информация"),
    PANEL("Панел"),
    TUHLA("Тухла");
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

}
