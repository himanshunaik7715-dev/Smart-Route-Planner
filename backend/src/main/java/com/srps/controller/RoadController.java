package com.srps.controller;

import com.srps.dto.CreateRoadRequest;
import com.srps.dto.RoadDto;
import com.srps.dto.RoadStatusRequest;
import com.srps.service.GraphService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    private final GraphService graphService;

    public RoadController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public List<RoadDto> list() {
        return graphService.listRoads();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoadDto create(@Valid @RequestBody CreateRoadRequest request) {
        return graphService.addRoad(request);
    }

    @PatchMapping("/{id}/status")
    public RoadDto status(@PathVariable String id, @RequestBody RoadStatusRequest request) {
        return graphService.setRoadStatus(id, request.isOpen());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        graphService.removeRoad(id);
    }
}
