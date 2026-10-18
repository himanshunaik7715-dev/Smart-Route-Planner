package com.srps.algorithms;

import com.srps.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BfsDfsTest {

    @Test
    void bfsTraversalIsLevelOrderAndCycleSafe() {
        Graph graph = AlgorithmFixtures.diamond();
        VisitResult visit = BFS.traverse(graph, "a");
        assertEquals("BFS", visit.getAlgorithm());
        assertEquals("a", visit.getVisitOrder().get(0));
        assertEquals(4, visit.getVisitOrder().size());
        assertEquals(4, new HashSet<>(visit.getVisitOrder()).size());
        assertFalse(visit.getVisitOrder().contains("z"));
        assertEquals(List.of("a", "b", "c"), visit.getVisitOrder().subList(0, 3));
    }

    @Test
    void bfsMinimumHopsPrefersFewerEdgesNotWeight() {
        PathResult hops = BFS.minHopPath(AlgorithmFixtures.demoSlice(), "college", "airport");
        assertTrue(hops.isReachable());
        assertEquals("BFS_MIN_HOPS", hops.getAlgorithm());
        assertEquals(List.of("college", "airport"), hops.getLocationIds());
        assertEquals(List.of("r14"), hops.getRoadIds());
        assertEquals(40.0, hops.getTotalDistanceKm());
    }

    @Test
    void bfsUnreachableAndSameNode() {
        Graph graph = AlgorithmFixtures.diamond();
        PathResult missing = BFS.minHopPath(graph, "a", "z");
        assertFalse(missing.isReachable());
        assertTrue(missing.getLocationIds().isEmpty());
        assertEquals(null, missing.getTotalDistanceKm());

        PathResult same = BFS.minHopPath(graph, "a", "a");
        assertTrue(same.isReachable());
        assertEquals(List.of("a"), same.getLocationIds());
        assertEquals(0.0, same.getTotalDistanceKm());
        assertTrue(BFS.reachable(graph, "a", "d"));
        assertFalse(BFS.reachable(graph, "a", "z"));
    }

    @Test
    void dfsFollowsInsertionOrderAndHandlesCycles() {
        Graph graph = AlgorithmFixtures.diamond();
        VisitResult visit = DFS.traverse(graph, "a");
        assertEquals("DFS", visit.getAlgorithm());
        assertEquals("a", visit.getVisitOrder().get(0));
        assertEquals(4, new HashSet<>(visit.getVisitOrder()).size());
        assertEquals(List.of("a", "b", "d", "c"), visit.getVisitOrder());
        assertFalse(visit.getVisitOrder().contains("z"));
    }

    @Test
    void closedRoadIsIgnoredByTraversals() {
        Graph graph = AlgorithmFixtures.diamond();
        graph.setRoadOpen("ab", false);
        VisitResult bfs = BFS.traverse(graph, "a");
        assertEquals(List.of("a", "c", "d", "b"), bfs.getVisitOrder());
        PathResult hops = BFS.minHopPath(graph, "a", "b");
        assertEquals(List.of("a", "c", "b"), hops.getLocationIds());
    }
}
