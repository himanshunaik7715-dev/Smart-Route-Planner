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
