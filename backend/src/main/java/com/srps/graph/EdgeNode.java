package com.srps.graph;

/**
 * Singly linked adjacency-list node. {@code next} is an implementation detail
 * and must never be serialised to JSON.
 */
public final class EdgeNode {

    private final int destinationIndex;
    private final int roadIndex;
    private final double distanceKm;
    private EdgeNode next;

    public EdgeNode(int destinationIndex, int roadIndex, double distanceKm, EdgeNode next) {
        this.destinationIndex = destinationIndex;
        this.roadIndex = roadIndex;
        this.distanceKm = distanceKm;
        this.next = next;
    }

    public int getDestinationIndex() {
        return destinationIndex;
    }

    public int getRoadIndex() {
        return roadIndex;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public EdgeNode getNext() {
        return next;
    }

    void setNext(EdgeNode next) {
        this.next = next;
    }
}
