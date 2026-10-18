package com.srps.controller;

import com.srps.dto.GraphDto;
import com.srps.service.GraphService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping
    public GraphDto graph() {
        return graphService.getGraph();
    }

    /**
     * Restores the canonical default network. The frontend calls this on load so
     * the mutable in-memory graph matches the built-in default it renders.
     */
    @PostMapping("/reset")
    public GraphDto reset() {
        graphService.resetToDefault();
        return graphService.getGraph();
    }
}
