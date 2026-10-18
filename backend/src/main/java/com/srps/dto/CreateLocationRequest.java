package com.srps.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateLocationRequest(
        String id,
        @NotBlank String name,
        @NotBlank String type,
        double x,
        double y
) {
}
