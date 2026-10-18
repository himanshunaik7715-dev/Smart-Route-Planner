package com.srps.algorithms;

import com.srps.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraTest {

    @Test
    void prefersCheaperIndirectPath() {
        PathResult path = Dijkstra.shortestPath(AlgorithmFixtures.diamond(), "a", "d");
        assertTrue(path.isReachable());
        assertEquals("DIJKSTRA", path.getAlgorithm());
        assertEquals(List.of("a", "b", "c", "d"), path.getLocationIds());
        assertEquals(3.0, path.getTotalDistanceKm());
        assertEquals(List.of("ab", "bc", "cd"), path.getRoadIds());
    }

    @Test
    void weightedPathDiffersFromMinimumHops() {
        Graph graph = AlgorithmFixtures.demoSlice();
        PathResult shortest = Dijkstra.shortestPath(graph, "college", "airport");
        PathResult hops = BFS.minHopPath(graph, "college", "airport");
        assertEquals(List.of("college", "bus_stop", "hospital", "park", "airport"), shortest.getLocationIds());
        assertEquals(14.0, shortest.getTotalDistanceKm());
        assertEquals(List.of("college", "airport"), hops.getLocationIds());
        assertEquals(40.0, hops.getTotalDistanceKm());
    }

    @Test
    void equalCostsKeepAValidReconstructedPath() {
        Graph graph = new Graph();
        graph.addLocation(new com.srps.model.Location("s", "S", "t", 0, 0));
        graph.addLocation(new com.srps.model.Location("a", "A", "t", 1, 0));
        graph.addLocation(new com.srps.model.Location("b", "B", "t", 0, 1));
        graph.addLocation(new com.srps.model.Location("t", "T", "t", 1, 1));
        graph.addRoad("sa", "s", "a", 2);
        graph.addRoad("sb", "s", "b", 2);
        graph.addRoad("at", "a", "t", 2);
        graph.addRoad("bt", "b", "t", 2);
        PathResult path = Dijkstra.shortestPath(graph, "s", "t");
        assertEquals(4.0, path.getTotalDistanceKm());
        assertEquals(3, path.getLocationIds().size());
        assertEquals("s", path.getLocationIds().get(0));
        assertEquals("t", path.getLocationIds().get(2));
    }

    @Test
    void unreachableSameNodeAndClosedRoad() {
        Graph graph = AlgorithmFixtures.diamond();
        PathResult missing = Dijkstra.shortestPath(graph, "a", "z");
        assertFalse(missing.isReachable());
        assertNull(missing.getTotalDistanceKm());

        PathResult same = Dijkstra.shortestPath(graph, "a", "a");
        assertEquals(0.0, same.getTotalDistanceKm());
        assertEquals(List.of("a"), same.getLocationIds());

        graph.setRoadOpen("bc", false);
        graph.setRoadOpen("cd", false);
        PathResult afterClose = Dijkstra.shortestPath(graph, "a", "d");
        assertEquals(101.0, afterClose.getTotalDistanceKm());
        assertEquals(List.of("a", "b", "d"), afterClose.getLocationIds());
    }
}
