package com.srps.model;

/**
 * A map vertex. Public {@code id} is stable for the API; the graph stores an
 * internal array index separately.
 */
public final class Location {

    private final String id;
    private final String name;
    private final String type;
    private final double x;
    private final double y;

    public Location(String id, String name, String type, double x, double y) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
