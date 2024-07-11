package com.property.crawler.property;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertySearchDto {
    private final static String WINDOWS_1251="Windows-1251";
    private final int act = 17;
    private final int actions = 2;
    private int actionType;//f1
    private int propertyType;//f2
    private String city;//f3 +
    private String location;//f4 +
    private int propertySize;//f5
    private final String button = "П О К А Ж И"; // +
    private final int sendActualPriceEveryMonthRadio = 2;

    public PropertySearchDto(int actionType, int propertyType, String city,
        String location, int propertySize) {
        this.actionType = actionType;
        this.propertyType = propertyType;
        this.city = city;
        this.location = location;
        this.propertySize = propertySize;
    }

    public String toQueryString() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        sb.append("act=").append(act).append("&");
        sb.append("actions=").append(actions).append("&");
        sb.append("f1=").append(actionType).append("&");
        sb.append("f2=").append(propertyType).append("&");
        sb.append("f3=").append(URLEncoder.encode(city, WINDOWS_1251)).append("&");
        sb.append("f4=").append(URLEncoder.encode(location, WINDOWS_1251)).append("&");
        sb.append("f5=").append(propertySize).append("&");
        sb.append("button=").append(URLEncoder.encode(button, WINDOWS_1251)).append("&");
        sb.append("f6=").append(sendActualPriceEveryMonthRadio);

        return sb.toString();
    }

    private String encodeParameter(String param) throws UnsupportedEncodingException {
        if (param == null) {
            return "";
        }
        return URLEncoder.encode(param, "UTF-8");
    }
}
