package com.srps.model;

/**
 * A bidirectional weighted road. Closed roads remain in the graph but are
 * skipped by every algorithm.
 */
public final class Road {

    private final String id;
    private final String fromLocationId;
    private final String toLocationId;
    private final double distanceKm;
    private boolean open;

    public Road(String id, String fromLocationId, String toLocationId, double distanceKm, boolean open) {
        this.id = id;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distanceKm = distanceKm;
        this.open = open;
    }

    public String getId() {
        return id;
    }

    public String getFromLocationId() {
        return fromLocationId;
    }

    public String getToLocationId() {
        return toLocationId;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
