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
    private String propertyConstructionType;
    private int propertySizeClean;
    private int numberOfRooms;
    private boolean hasGarage;
    private boolean hasParkingSpot;
    private String orientation;
    private int price;

}
