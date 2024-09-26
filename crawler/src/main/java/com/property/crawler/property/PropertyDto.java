package com.property.crawler.property;

import java.time.LocalDateTime;
import java.util.List;

import com.property.crawler.enums.PropertyType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyDto {

    private String city;
    private String mainLocation;
    private List<String> neighbourLocations;
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

    @Override
    public String toString() {
        return "PropertyDto - " +
            "  City: '" + city + '\'' +
            ", Main Location: '" + mainLocation + '\'' +
            ", Neighbour Locations: '" + neighbourLocations + '\'' +
            ", Property Type: " + PropertyType.getValueById(propertyType) +
            ", Property Size: " + propertySize +
            ", Construction Type: '" + constructionType + '\'' +
            ", Has Garage: '" + hasGarage + '\'' +
            ", Floor Info: '" + floorInfo + '\'' +
            ", Price: '" + price;
    }
}
