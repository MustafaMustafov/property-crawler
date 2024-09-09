package com.property.crawler.property;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PropertyDtoFormVersion {

    private String city;
    private String mainLocation;
    private List<String> neighbourLocations;
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
