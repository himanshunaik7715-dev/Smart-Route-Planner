package com.srps.algorithms;

import java.util.List;

public final class PathResult {

    private final String algorithm;
    private final String sourceId;
    private final String destinationId;
    private final boolean reachable;
    private final List<String> locationIds;
    private final List<String> roadIds;
    private final Double totalDistanceKm;

    public PathResult(
            String algorithm,
            String sourceId,
            String destinationId,
            boolean reachable,
            List<String> locationIds,
            List<String> roadIds,
            Double totalDistanceKm
    ) {
        this.algorithm = algorithm;
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        this.reachable = reachable;
        this.locationIds = locationIds;
        this.roadIds = roadIds;
        this.totalDistanceKm = totalDistanceKm;
    }

    public static PathResult unreachable(String algorithm, String sourceId, String destinationId) {
        return new PathResult(algorithm, sourceId, destinationId, false, List.of(), List.of(), null);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public boolean isReachable() {
        return reachable;
    }

    public List<String> getLocationIds() {
        return locationIds;
    }

    public List<String> getRoadIds() {
        return roadIds;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }
}
