package com.srps.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRoadRequest(
        String id,
        @NotBlank String fromLocationId,
        @NotBlank String toLocationId,
        double distanceKm
) {
}
