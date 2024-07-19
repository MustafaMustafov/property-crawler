
package com.property.crawler.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum Neighborhood {
    BLAGOEVGRAD("Благоевград", Arrays.asList("Ален Мак", "Баларбаши", "Бялата висота", "Вароша", "Втора промишлена зона", "Грамада", "Еленово 1", "Еленово 2", "Запад", "Идеален център", "Орлова чука", "Освобождение", "Първа промишлена зона", "Струмско", "Широк център")),
    BURGAS("Бургас", Arrays.asList("Акациите", "Банева", "Братя Миладинови", "Ветрен", "Възраждане", "Горно Езерово", "Долно Езерово", "Зорница", "Изгрев", "Крайморие", "Лазур", "Летище Бургас", "Лозово", "Меден рудник - зона А", "Меден рудник - зона Б", "Меден рудник - зона В", "Меден рудник - зона Г", "Меден рудник - зона Д", "Пети километър", "Победа", "Пристанище Бургас", "Промишлена зона - Лозово", "Промишлена зона - Север", "Промишлена зона - ЮГ", "Рудник", "Сарафово", "Свобода", "Славейков", "Център","Черно море", "в.з.Боровете", "в.з.Брястовете","в.з.Димчево", "в.з.Лозница", "в.з.Острица", "в.з.Черниците", "гр. Българово")),

    VARNA("Варна", Arrays.asList("Гранд Мол", "Бриз")),
    VELIKO_TURNOVO("Велико Търново", Arrays.asList("Квартал 1", "Квартал 2")),
    VIDIN("Видин", Arrays.asList("Квартал 1", "Квартал 2")),
    VRATSA("Враца", Arrays.asList("Квартал 1", "Квартал 2")),
    GABROVO("Габрово", Arrays.asList("Квартал 1", "Квартал 2")),
    DOBRICH("Добрич", Arrays.asList("Квартал 1", "Квартал 2")),
    KARDZHALI("Кърджали", Arrays.asList("Квартал 1", "Квартал 2")),
    KYUSTENDIL("Кюстендил", Arrays.asList("Квартал 1", "Квартал 2")),
    LOVECH("Ловеч", Arrays.asList("Квартал 1", "Квартал 2")),
    MONTANA("Монтана", Arrays.asList("Квартал 1", "Квартал 2")),
    PAZARDZHIK("Пазарджик", Arrays.asList("Квартал 1", "Квартал 2")),
    PERNIK("Перник", Arrays.asList("Квартал 1", "Квартал 2")),
    PLEVEN("Плевен", Arrays.asList("Квартал 1", "Квартал 2")),
    PLOVDIV("Пловдив", Arrays.asList("Квартал 1", "Квартал 2")),
    RAZGRAD("Разград", Arrays.asList("Квартал 1", "Квартал 2")),
    RUSE("Русе", Arrays.asList("Квартал 1", "Квартал 2")),
    SILISTRA("Силистра", Arrays.asList("Квартал 1", "Квартал 2")),
    SLIVEN("Сливен", Arrays.asList("Квартал 1", "Квартал 2")),
    SMOLYAN("Смолян", Arrays.asList("Квартал 1", "Квартал 2")),
    SOFIA("София", Arrays.asList("Квартал 1", "Квартал 2")),
    STARA_ZAGORA("Стара Загора", Arrays.asList("Квартал 1", "Квартал 2")),
    TARGOVISHTE("Търговище", Arrays.asList("Квартал 1", "Квартал 2")),
    HASKOVO("Хасково", Arrays.asList("Хасково 1", "Хасково 2")),
    SHUMEN("Шумен", Arrays.asList("Квартал 1", "Квартал 2")),
    YAMBOL("Ямбол", Arrays.asList("Ямбол 1", "Ямбол 2"));

    @Getter
    private final String cityName;
    @Getter
    private final List<String> neighborhoods;

    Neighborhood(String cityName, List<String> neighborhoods) {
        this.cityName = cityName;
        this.neighborhoods = neighborhoods;
    }

    public static List<String> getNeighborhoodsByTown(String townName) {
        for (Neighborhood neighborhood : values()) {
            if (neighborhood.getCityName().equalsIgnoreCase(townName)) {
                return neighborhood.getNeighborhoods();
            }
        }
        return null;
    }
}