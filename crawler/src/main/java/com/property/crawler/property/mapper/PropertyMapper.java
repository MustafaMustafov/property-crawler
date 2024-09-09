package com.property.crawler.property.mapper;

import com.property.crawler.property.Property;
import com.property.crawler.property.PropertyDto;
import java.util.ArrayList;
import java.util.List;

public class PropertyMapper {

    PropertyMapper() {

    }

    public static PropertyDto toDto(Property property) {
        PropertyDto propertyDto = new PropertyDto();

        propertyDto.setPropertyType(property.getPropertyType().getId());
        propertyDto.setMainLocation(property.getLocation());
        propertyDto.setCity(property.getCity());
        propertyDto.setPrice(property.getPrice());
        propertyDto.setPropertySize((int) property.getTotalSize());
        propertyDto.setFloorInfo(property.getFloorNumber());
        propertyDto.setHasGarage(Boolean.TRUE.equals(property.getGarageOrParking()) ? "да" : "не");
        propertyDto.setPublicationDateAndTime(property.getPublicationDateAndTime());
        propertyDto.setPropertyUrl(property.getPropertyUrl());

        return propertyDto;
    }

    public static List<PropertyDto> toDtos(List<Property> properties) {
        List<PropertyDto> propertyDtos = new ArrayList<>();

        properties.forEach(property -> {
            propertyDtos.add(toDto(property));
        });

        return propertyDtos;
    }

}
