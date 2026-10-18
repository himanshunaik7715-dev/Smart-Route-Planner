package com.srps.service;

import com.srps.algorithms.BFS;
import com.srps.algorithms.DFS;
import com.srps.algorithms.Dijkstra;
import com.srps.dto.ReachableDto;
import com.srps.dto.RouteResultDto;
import com.srps.dto.TraversalResultDto;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

    private final GraphService graphService;

    public RouteService(GraphService graphService) {
        this.graphService = graphService;
    }

    public RouteResultDto shortest(String sourceId, String destinationId) {
        synchronized (graphService) {
            graphService.requireLocation(sourceId);
            graphService.requireLocation(destinationId);
            return RouteResultDto.from(Dijkstra.shortestPath(graphService.graph(), sourceId, destinationId));
        }
    }

    public RouteResultDto minHops(String sourceId, String destinationId) {
        synchronized (graphService) {
            graphService.requireLocation(sourceId);
            graphService.requireLocation(destinationId);
            return RouteResultDto.from(BFS.minHopPath(graphService.graph(), sourceId, destinationId));
        }
    }

    public TraversalResultDto bfs(String sourceId) {
        synchronized (graphService) {
            graphService.requireLocation(sourceId);
            return TraversalResultDto.from(BFS.traverse(graphService.graph(), sourceId));
        }
    }

    public TraversalResultDto dfs(String sourceId) {
        synchronized (graphService) {
            graphService.requireLocation(sourceId);
            return TraversalResultDto.from(DFS.traverse(graphService.graph(), sourceId));
        }
    }

    public ReachableDto reachable(String sourceId, String destinationId) {
        synchronized (graphService) {
            graphService.requireLocation(sourceId);
            graphService.requireLocation(destinationId);
            boolean reachable = BFS.reachable(graphService.graph(), sourceId, destinationId);
            return new ReachableDto(sourceId, destinationId, reachable);
        }
    }
}
