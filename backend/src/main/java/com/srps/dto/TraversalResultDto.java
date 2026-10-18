package com.srps.dto;

import com.srps.algorithms.VisitResult;

import java.util.List;

public record TraversalResultDto(String algorithm, String sourceId, List<String> visitOrder) {

    public static TraversalResultDto from(VisitResult result) {
        return new TraversalResultDto(result.getAlgorithm(), result.getSourceId(), result.getVisitOrder());
    }
}
