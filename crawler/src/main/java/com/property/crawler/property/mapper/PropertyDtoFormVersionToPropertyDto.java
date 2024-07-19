package com.property.crawler.property.mapper;

import com.property.crawler.property.PropertyDto;
import com.property.crawler.property.PropertyDtoFormVersion;

public class PropertyDtoFormVersionToPropertyDto {

    public static PropertyDto toPropertyDto(PropertyDtoFormVersion dtoFormVersion){
        PropertyDto propertyDto = new PropertyDto();

        propertyDto.setCity(dtoFormVersion.getCity());
        propertyDto.setLocation(dtoFormVersion.getLocation());
        propertyDto.setPropertyType(dtoFormVersion.getPropertyType());
        propertyDto.setPropertySize(dtoFormVersion.getPropertySize());
        propertyDto.setNumberOfRooms(dtoFormVersion.getNumberOfRooms());
        propertyDto.setNumberOfBedrooms(dtoFormVersion.getNumberOfBedrooms());
        propertyDto.setNumberOfBathrooms(dtoFormVersion.getNumberOfBathrooms());
        propertyDto.setFloorInfo(null);
        propertyDto.setOrientation(dtoFormVersion.getOrientation());
        propertyDto.setPrice(String.valueOf(dtoFormVersion.getPrice()));
        propertyDto.setHasGarage(dtoFormVersion.isHasGarage() ? "да" : " не");

        return propertyDto;

    }
}
