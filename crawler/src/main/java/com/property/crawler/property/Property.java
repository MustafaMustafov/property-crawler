package com.property.crawler.property;

import com.property.crawler.enums.ConstructionType;
import com.property.crawler.enums.PropertyType;
import com.sleepycat.persist.model.Entity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Property {

    private String title;
    private PropertyType propertyType;
    private ConstructionType constructionType;
    private String city;
    private String location;
    private double cleanSize;
    private double totalSize;
    private Integer bathCount;
    private Boolean garageOrParking;
    private String floorNumber;
    private String price;
    private String pricePerSqM;
    private String description;
    private LocalDateTime publicationDateAndTime;
    private String propertyUrl;
}
