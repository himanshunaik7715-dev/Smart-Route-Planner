package com.srps.graph;

import com.srps.exception.ApiException;
import com.srps.model.Location;
import com.srps.model.Road;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom weighted adjacency-list graph.
 *
 * <p>Invariant: location public IDs are stable; internal indices may change
 * only if we compact arrays (MVP never deletes vertices). Each bidirectional
 * road occupies one {@link Road} record and two {@link EdgeNode} entries,
 * one at each endpoint, sharing the same {@code roadIndex}.
 */
public final class Graph {

    private static final int INITIAL_CAPACITY = 16;

    private Location[] locations = new Location[INITIAL_CAPACITY];
    private EdgeNode[] adjacencyHeads = new EdgeNode[INITIAL_CAPACITY];
    private Road[] roads = new Road[INITIAL_CAPACITY];
    private final Map<String, Integer> locationIndexById = new HashMap<>();
    private final Map<String, Integer> roadIndexById = new HashMap<>();
    private int vertexCount;
    private int roadCount;

    public int getVertexCount() {
        return vertexCount;
    }

    public int getRoadCount() {
        return roadCount;
    }

    /** Empties the graph so it can be re-seeded with the canonical network. */
    public void clear() {
        locations = new Location[INITIAL_CAPACITY];
        adjacencyHeads = new EdgeNode[INITIAL_CAPACITY];
        roads = new Road[INITIAL_CAPACITY];
        locationIndexById.clear();
        roadIndexById.clear();
        vertexCount = 0;
        roadCount = 0;
    }

    public Location getLocation(int index) {
        ensureVertexIndex(index);
        return locations[index];
    }

    public Road getRoad(int index) {
        ensureRoadIndex(index);
        return roads[index];
    }

    public EdgeNode getAdjacencyHead(int vertexIndex) {
        ensureVertexIndex(vertexIndex);
        return adjacencyHeads[vertexIndex];
    }

    public int indexOfLocation(String locationId) {
        Integer index = locationIndexById.get(locationId);
        if (index == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Unknown location ID: " + locationId);
        }
        return index;
    }

    public boolean hasLocation(String locationId) {
        return locationIndexById.containsKey(locationId);
    }

    public int indexOfRoad(String roadId) {
        Integer index = roadIndexById.get(roadId);
        if (index == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Unknown road ID: " + roadId);
        }
        return index;
    }

    public List<Location> listLocations() {
        List<Location> result = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            result.add(locations[i]);
        }
        return result;
    }

    public List<Road> listRoads() {
        List<Road> result = new ArrayList<>(roadCount);
        for (int i = 0; i < roadCount; i++) {
            if (roads[i] != null) {
                result.add(roads[i]);
            }
        }
        return result;
    }

    public void addLocation(Location location) {
        if (location.getId() == null || location.getId().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Location ID must be non-empty.");
        }
        if (location.getName() == null || location.getName().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Location name must be non-empty.");
        }
        if (locationIndexById.containsKey(location.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Location already exists: " + location.getId());
        }
        ensureVertexCapacity(vertexCount + 1);
        int index = vertexCount;
        locations[index] = location;
        adjacencyHeads[index] = null;
        locationIndexById.put(location.getId(), index);
        vertexCount++;
    }

    public Road addRoad(String roadId, String fromLocationId, String toLocationId, double distanceKm) {
        validateDistance(distanceKm);
        if (roadId == null || roadId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Road ID must be non-empty.");
        }
        if (fromLocationId.equals(toLocationId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A road must connect two different locations.");
        }
        if (roadIndexById.containsKey(roadId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Road already exists: " + roadId);
        }
        int from = indexOfLocation(fromLocationId);
        int to = indexOfLocation(toLocationId);
        if (undirectedRoadExists(from, to)) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "A road already exists between " + fromLocationId + " and " + toLocationId + ".");
        }

        ensureRoadCapacity(roadCount + 1);
        int roadIndex = roadCount;
        Road road = new Road(roadId, fromLocationId, toLocationId, distanceKm, true);
        roads[roadIndex] = road;
        roadIndexById.put(roadId, roadIndex);
        roadCount++;

        appendEdge(from, new EdgeNode(to, roadIndex, distanceKm, null));
        appendEdge(to, new EdgeNode(from, roadIndex, distanceKm, null));
        return road;
    }

    public void setRoadOpen(String roadId, boolean open) {
        Road road = roads[indexOfRoad(roadId)];
        road.setOpen(open);
    }

    public void removeRoad(String roadId) {
        int roadIndex = indexOfRoad(roadId);
        Road road = roads[roadIndex];
        int from = indexOfLocation(road.getFromLocationId());
        int to = indexOfLocation(road.getToLocationId());
        adjacencyHeads[from] = removeEdgesForRoad(adjacencyHeads[from], roadIndex);
        adjacencyHeads[to] = removeEdgesForRoad(adjacencyHeads[to], roadIndex);
        // Leave a hole in the roads array so remaining EdgeNode.roadIndex values stay valid.
        roads[roadIndex] = null;
        roadIndexById.remove(roadId);
    }

    /**
     * Open-edge filter shared by BFS, DFS, and Dijkstra. Closed roads stay in
     * the adjacency list so reopening does not rebuild pointers.
     */
    public boolean isOpenEdge(EdgeNode edge) {
        return roads[edge.getRoadIndex()].isOpen();
    }

    private boolean undirectedRoadExists(int from, int to) {
        EdgeNode cursor = adjacencyHeads[from];
        while (cursor != null) {
            if (cursor.getDestinationIndex() == to) {
                return true;
            }
            cursor = cursor.getNext();
        }
        return false;
    }

    private void appendEdge(int vertexIndex, EdgeNode node) {
        EdgeNode head = adjacencyHeads[vertexIndex];
        if (head == null) {
            adjacencyHeads[vertexIndex] = node;
            return;
        }
        EdgeNode cursor = head;
        while (cursor.getNext() != null) {
            cursor = cursor.getNext();
        }
        cursor.setNext(node);
    }

    private EdgeNode removeEdgesForRoad(EdgeNode head, int roadIndex) {
        EdgeNode dummy = new EdgeNode(-1, -1, 0, head);
        EdgeNode cursor = dummy;
        while (cursor.getNext() != null) {
            if (cursor.getNext().getRoadIndex() == roadIndex) {
                cursor.setNext(cursor.getNext().getNext());
            } else {
                cursor = cursor.getNext();
            }
        }
        return dummy.getNext();
    }

    private void ensureVertexCapacity(int needed) {
        if (needed <= locations.length) {
            return;
        }
        int newCap = Math.max(needed, locations.length * 2);
        locations = Arrays.copyOf(locations, newCap);
        adjacencyHeads = Arrays.copyOf(adjacencyHeads, newCap);
    }

    private void ensureRoadCapacity(int needed) {
        if (needed <= roads.length) {
            return;
        }
        roads = Arrays.copyOf(roads, Math.max(needed, roads.length * 2));
    }

    private void ensureVertexIndex(int index) {
        if (index < 0 || index >= vertexCount) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid vertex index: " + index);
        }
    }

    private void ensureRoadIndex(int index) {
        if (index < 0 || index >= roadCount) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid road index: " + index);
        }
    }

    public static void validateDistance(double distanceKm) {
        if (!Double.isFinite(distanceKm) || distanceKm <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Road distance must be a finite number greater than zero kilometres.");
        }
    }
}
