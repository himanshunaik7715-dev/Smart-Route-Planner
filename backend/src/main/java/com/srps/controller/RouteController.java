package com.srps.controller;

import com.srps.dto.ReachableDto;
import com.srps.dto.RouteResultDto;
import com.srps.dto.TraversalResultDto;
import com.srps.service.RouteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/shortest")
    public RouteResultDto shortest(
            @RequestParam String sourceId,
            @RequestParam String destinationId
    ) {
        return routeService.shortest(sourceId, destinationId);
    }

    @GetMapping("/min-hops")
    public RouteResultDto minHops(
            @RequestParam String sourceId,
            @RequestParam String destinationId
    ) {
        return routeService.minHops(sourceId, destinationId);
    }

    @GetMapping("/bfs")
    public TraversalResultDto bfs(@RequestParam String sourceId) {
        return routeService.bfs(sourceId);
    }

    @GetMapping("/dfs")
    public TraversalResultDto dfs(@RequestParam String sourceId) {
        return routeService.dfs(sourceId);
    }

    @GetMapping("/reachable")
    public ReachableDto reachable(
            @RequestParam String sourceId,
            @RequestParam String destinationId
    ) {
        return routeService.reachable(sourceId, destinationId);
    }
}
