package com.srps.service;

import com.srps.dto.CreateLocationRequest;
import com.srps.dto.CreateRoadRequest;
import com.srps.dto.GraphDto;
import com.srps.dto.LocationDto;
import com.srps.dto.RoadDto;
import com.srps.exception.ApiException;
import com.srps.graph.Graph;
import com.srps.model.Location;
import com.srps.model.Road;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GraphService {

    private final Graph graph = new Graph();
    private final AtomicInteger generatedLocationSuffix = new AtomicInteger(1);
    private final AtomicInteger generatedRoadSuffix = new AtomicInteger(1);

    public Graph graph() {
        return graph;
    }

    public synchronized List<LocationDto> listLocations() {
        return graph.listLocations().stream().map(LocationDto::from).toList();
    }

    public synchronized List<RoadDto> listRoads() {
        return graph.listRoads().stream().map(RoadDto::from).toList();
    }

    public synchronized GraphDto getGraph() {
        return new GraphDto(listLocations(), listRoads());
    }

    public synchronized LocationDto addLocation(CreateLocationRequest request) {
        String id = request.id() == null || request.id().isBlank()
                ? uniqueLocationId(request.name())
                : request.id().trim();
        graph.addLocation(new Location(id, request.name().trim(), request.type().trim(), request.x(), request.y()));
        return LocationDto.from(graph.getLocation(graph.indexOfLocation(id)));
    }

    public synchronized RoadDto addRoad(CreateRoadRequest request) {
        String id = request.id() == null || request.id().isBlank()
                ? "r-" + generatedRoadSuffix.getAndIncrement()
                : request.id().trim();
        Road road = graph.addRoad(id, request.fromLocationId(), request.toLocationId(), request.distanceKm());
        return RoadDto.from(road);
    }

    public synchronized RoadDto setRoadStatus(String roadId, boolean open) {
        graph.setRoadOpen(roadId, open);
        return RoadDto.from(graph.getRoad(graph.indexOfRoad(roadId)));
    }

    public synchronized void removeRoad(String roadId) {
        graph.removeRoad(roadId);
    }

    /**
     * Clears the graph and re-applies the canonical 10-location / 16-road demo
     * network. The graph is a mutable in-memory singleton, so it drifts as roads
     * are added, removed or closed. The frontend always renders this same default
     * network on load, so it calls this to keep both sides in sync.
     */
    public synchronized void resetToDefault() {
        graph.clear();
        generatedLocationSuffix.set(1);
        generatedRoadSuffix.set(1);

        seedLocation("college", "College", "campus", 120, 420);
        seedLocation("station", "Station", "transit", 280, 280);
        seedLocation("mall", "Mall", "commercial", 480, 180);
        seedLocation("hospital", "Hospital", "medical", 500, 420);
        seedLocation("bus_stop", "Bus Stop", "transit", 280, 540);
        seedLocation("park", "Park", "park", 700, 300);
        seedLocation("hotel", "Hotel", "lodging", 680, 520);
        seedLocation("airport", "Airport", "airport", 900, 200);
        seedLocation("library", "Library", "library", 140, 180);
        seedLocation("restaurant", "Restaurant", "dining", 860, 480);

        seedRoad("r1", "college", "station", 3.0);
        seedRoad("r2", "college", "bus_stop", 2.5);
        seedRoad("r3", "college", "library", 2.0);
        seedRoad("r4", "station", "mall", 4.0);
        seedRoad("r5", "station", "hospital", 4.0);
        seedRoad("r6", "bus_stop", "hospital", 3.5);
        seedRoad("r7", "mall", "park", 6.0);
        seedRoad("r8", "hospital", "park", 3.0);
        seedRoad("r9", "hospital", "hotel", 4.5);
        seedRoad("r10", "park", "airport", 5.0);
        seedRoad("r11", "hotel", "airport", 8.0);
        seedRoad("r12", "hotel", "restaurant", 2.0);
        seedRoad("r13", "library", "mall", 5.5);
        seedRoad("r14", "college", "airport", 40.0);
        seedRoad("r15", "mall", "airport", 18.0);
        seedRoad("r16", "bus_stop", "hotel", 9.0);
    }

    public synchronized void seedLocation(String id, String name, String type, double x, double y) {
        graph.addLocation(new Location(id, name, type, x, y));
    }

    public synchronized void seedRoad(String id, String from, String to, double distanceKm) {
        graph.addRoad(id, from, to, distanceKm);
    }

    public void requireLocation(String locationId) {
        if (locationId == null || locationId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Location ID is required.");
        }
        graph.indexOfLocation(locationId);
    }

    private String uniqueLocationId(String name) {
        String slug = name.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        if (slug.isBlank()) {
            slug = "location";
        }
        String candidate = slug;
        while (graph.hasLocation(candidate)) {
            candidate = slug + "-" + generatedLocationSuffix.getAndIncrement();
        }
        return candidate;
    }
}
