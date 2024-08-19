package com.property.crawler.property;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PropertyDtoFormVersion {

    private String city;
    private String location;
    private int actionType;
    private int propertyType;
    private int propertySize;
    private int numberOfRooms;
    private int numberOfBedrooms;
    private int numberOfBathrooms;
    private boolean hasGarage;
    private String orientation;
    private int price;

}
