package com.property.crawler.property;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyDto {

    private String city;
    private String location;
    private int propertyType;
    private int propertySize;
    private int propertySizeClean;
    private String constructionType;
    private int numberOfRooms;
    private int numberOfBedrooms;
    private int numberOfBathrooms;
    private String hasGarage;
    private String floorInfo;
    private String orientation;
    private String price;
    private LocalDateTime publicationDateAndTime;
    private String propertyUrl;
}
