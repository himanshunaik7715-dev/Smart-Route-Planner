package com.srps.dto;

import com.srps.model.Location;

public record LocationDto(String id, String name, String type, double x, double y) {

    public static LocationDto from(Location location) {
        return new LocationDto(
                location.getId(),
                location.getName(),
                location.getType(),
                location.getX(),
                location.getY()
        );
    }
}
