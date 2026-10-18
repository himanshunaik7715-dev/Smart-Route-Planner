package com.srps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srps.model.Road;

public record RoadDto(
        String id,
        String fromLocationId,
        String toLocationId,
        double distanceKm,
        @JsonProperty("isOpen") boolean isOpen
) {

    public static RoadDto from(Road road) {
        return new RoadDto(
                road.getId(),
                road.getFromLocationId(),
                road.getToLocationId(),
                road.getDistanceKm(),
                road.isOpen()
        );
    }
}
