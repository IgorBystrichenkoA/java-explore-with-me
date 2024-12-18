package ru.practicum.ewm.event;

import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Location;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocationMapper {

    public LocationDto toLocationDtoFromLocation(Location location) {

        LocationDto locationDto = new LocationDto();

        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());

        return locationDto;
    }



}
