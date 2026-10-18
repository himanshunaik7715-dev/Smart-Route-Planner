package com.srps.algorithms;

import java.util.List;

public final class VisitResult {

    private final String algorithm;
    private final String sourceId;
    private final List<String> visitOrder;

    public VisitResult(String algorithm, String sourceId, List<String> visitOrder) {
        this.algorithm = algorithm;
        this.sourceId = sourceId;
        this.visitOrder = visitOrder;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getSourceId() {
        return sourceId;
    }

    public List<String> getVisitOrder() {
        return visitOrder;
    }
}
