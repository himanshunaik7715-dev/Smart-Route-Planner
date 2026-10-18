package com.srps.dto;

import java.util.List;

public record GraphDto(List<LocationDto> locations, List<RoadDto> roads) {
}
