package com.property.crawler.property;

import static com.property.crawler.imotbg.ImotBGService.WINDOWS_1251;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PropertySearchDtoNewRequest {

    private static final int ACT = 3;
    private static final int RUB = 1;
    private static final int TOP_MENU = 2;
    private static final int ACTIONS = 1;
    private static final int F_1 = 1;
    private static final int F_4 = 1;
    private static final String CURRENCY = "EUR";
    private int propertyType;
    private int propertySize;
    private String city;
    private String location;
    private String constructionType;
    private boolean hasGarage;
    private boolean hasParkingSpot;

    public PropertySearchDtoNewRequest(int propertyType, int propertySize,
        String city, String location, String constructionType, boolean hasGarage, boolean hasParkingSpot) {
        this.propertyType = propertyType;
        this.propertySize = propertySize;
        this.city = city;
        this.location = location;
        this.constructionType = constructionType;
        this.hasGarage = hasGarage;
        this.hasParkingSpot = hasParkingSpot;
    }

    public String toQueryString() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        sb.append("act=").append(ACT).append("&");
        sb.append("rub=").append(RUB).append("&");
        sb.append("topmenu=").append(TOP_MENU).append("&");
        sb.append("actions=").append(ACTIONS).append("&");
        sb.append("f1=").append(F_1).append("&");
        sb.append("f4=").append(F_4).append("&");
        sb.append("f7=").append(propertyType).append("%7E&");
        sb.append("f30=").append(CURRENCY).append("&");
        sb.append("f26=").append(propertySize * 0.8).append("&");
        sb.append("f27=").append(propertySize * 1.2).append("&");
        sb.append("f28=").append(URLEncoder.encode(city, WINDOWS_1251)).append("&");
        sb.append("f40=").append(URLEncoder.encode(location, WINDOWS_1251)).append("&");
        sb.append(
            constructionType.equalsIgnoreCase("панел") ? "f61=" + URLEncoder.encode(constructionType, WINDOWS_1251)
                : "f62=" + URLEncoder.encode(constructionType, WINDOWS_1251)).append("&");
        sb.append("f70=").append(hasGarage ? "%D1+%E3%E0%F0%E0%E6" : "").append("&");
        sb.append("f71=").append(hasParkingSpot ? "%D1+%EF%E0%F0%EA%E8%ED%E3" : "").append("&");

        return sb.toString();
    }
}
