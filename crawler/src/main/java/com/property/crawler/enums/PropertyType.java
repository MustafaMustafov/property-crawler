package com.property.crawler.enums;

public enum PropertyType {
    UNKNOWN(0, ""),
    TYPE_1(1, "1-СТАЕН"),
    TYPE_2(2, "2-СТАЕН"),
    TYPE_3(3, "3-СТАЕН"),
    TYPE_4(4, "4-СТАЕН"),
    MNOSTAEN(5, "МНОГОСТАЕН"),
    MEZONET(6, "МЕЗОНЕТ"),
    OFFICE(7, "ОФИС"),
    ATELIE_TAVAN(8, "АТЕЛИЕ, ТАВАН"),
    ETAGE_OT_KASHTA(9, "ЕТАЖ ОТ КЪЩА"),
    HOUSE(10, "КЪЩА"),
    VILLA(11, "ВИЛА"),
    MAGAZIN(12, "МАГАЗИН"),
    ZAVEDENIE(13, "ЗАВЕДЕНИЕ"),
    SKLAD(14, "СКЛАД"),
    GARAGE_PARKING(15, "ГАРАЖ, ПАРКОМЯСТО"),
    PROM_POMESHENIE(16, "ПРОМ. ПОМЕЩЕНИЕ"),
    HOTEL(17, "ХОТЕЛ"),
    PARCEL(18, "ПАРЦЕЛ"),
    ZEMEDELSKA_ZEMYA(19, "ЗЕМЕДЕЛСКА ЗЕМЯ");

    private final int id;
    private final String value;

    PropertyType(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static String getValueById(int id) {
        for (PropertyType type : PropertyType.values()) {
            if (type.id == id) {
                return type.value;
            }
        }
        return UNKNOWN.value;
    }

    public static PropertyType getById(int id) {
        for (PropertyType type : PropertyType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
