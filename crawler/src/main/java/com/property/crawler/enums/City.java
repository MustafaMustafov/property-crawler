package com.property.crawler.enums;

import java.util.Objects;

public enum City {
    BLAGOEVGRAD("град Благоевград"),
    BURGAS("град Бургас"),
    VARNA("град Варна"),
    VELIKO_TURNOVO("град Велико Търново"),
    VIDIN("град Видин"),
    VRATSA("град Враца"),
    GABROVO("град Габрово"),
    DOBRICH("град Добрич"),
    KARDZHALI("град Кърджали"),
    KYUSTENDIL("град Кюстендил"),
    LOVECH("град Ловеч"),
    MONTANA("град Монтана"),
    PAZARDZHIK("град Пазарджик"),
    PERNIK("град Перник"),
    PLEVEN("град Плевен"),
    PLOVDIV("град Пловдив"),
    RAZGRAD("град Разград"),
    RUSE("град Русе"),
    SILISTRA("град Силистра"),
    SLIVEN("град Сливен"),
    SMOLYAN("град Смолян"),
    SOFIA("град София"),
    STARA_ZAGORA("град Стара Загора"),
    TARGOVISHTE("град Търговище"),
    HASKOVO("град Хасково"),
    SHUMEN("град Шумен"),
    YAMBOL("град Ямбол");

    private final String cityName;

    City(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public static String getByCityName(String cityName) {
        for (City city : City.values()) {
            if (Objects.equals(city.cityName.substring(5), cityName)) {
                return city.getCityName();
            }
        }
        return "Not found";

    }

}
