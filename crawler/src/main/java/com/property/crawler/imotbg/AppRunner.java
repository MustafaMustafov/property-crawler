package com.property.crawler.imotbg;

import com.property.crawler.enums.City;

public class AppRunner {

    public static void main(String[] args) {
        ImotBGService imotBGService1 = new ImotBGService();
        imotBGService1.getProperty(1, 3,
            City.VARNA.getCityName(), "Център", 90);
    }

}

