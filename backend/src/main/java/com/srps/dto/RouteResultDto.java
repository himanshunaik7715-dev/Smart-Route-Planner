package com.srps.dto;

import com.srps.algorithms.PathResult;

import java.util.List;

public record RouteResultDto(
        String algorithm,
        String sourceId,
        String destinationId,
        boolean reachable,
        List<String> locationIds,
        List<String> roadIds,
        Double totalDistanceKm
) {

    public static RouteResultDto from(PathResult result) {
        return new RouteResultDto(
                result.getAlgorithm(),
                result.getSourceId(),
                result.getDestinationId(),
                result.isReachable(),
                result.getLocationIds(),
                result.getRoadIds(),
                result.getTotalDistanceKm()
        );
    }
}
