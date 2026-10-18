package com.srps.controller;

import com.srps.dto.CreateLocationRequest;
import com.srps.dto.LocationDto;
import com.srps.service.GraphService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final GraphService graphService;

    public LocationController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public List<LocationDto> list() {
        return graphService.listLocations();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto create(@Valid @RequestBody CreateLocationRequest request) {
        return graphService.addLocation(request);
    }
}
