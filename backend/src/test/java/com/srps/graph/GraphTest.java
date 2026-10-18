package com.srps.graph;

import com.srps.exception.ApiException;
import com.srps.model.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphTest {

    @Test
    void addsBidirectionalAdjacency() {
        Graph graph = smallGraph();
        int a = graph.indexOfLocation("a");
        int b = graph.indexOfLocation("b");
        EdgeNode fromA = graph.getAdjacencyHead(a);
        EdgeNode fromB = graph.getAdjacencyHead(b);
        assertEquals(b, fromA.getDestinationIndex());
        assertEquals(a, fromB.getDestinationIndex());
        assertEquals(fromA.getRoadIndex(), fromB.getRoadIndex());
    }

    @Test
    void rejectsNonPositiveDistance() {
        Graph graph = new Graph();
        graph.addLocation(new Location("a", "A", "test", 0, 0));
        graph.addLocation(new Location("b", "B", "test", 1, 1));
        assertThrows(ApiException.class, () -> graph.addRoad("r1", "a", "b", 0));
        assertThrows(ApiException.class, () -> graph.addRoad("r1", "a", "b", -2));
        assertThrows(ApiException.class, () -> graph.addRoad("r1", "a", "b", Double.NaN));
        assertThrows(ApiException.class, () -> graph.addRoad("r1", "a", "b", Double.POSITIVE_INFINITY));
    }

    @Test
    void rejectsDuplicateRoadAndUnknownLocation() {
        Graph graph = smallGraph();
        assertThrows(ApiException.class, () -> graph.addRoad("r2", "a", "b", 4));
        assertThrows(ApiException.class, () -> graph.addRoad("r3", "a", "missing", 4));
        assertThrows(ApiException.class, () -> graph.addRoad("r3", "a", "a", 4));
    }

    @Test
    void closeReopenAndRemoveRoad() {
        Graph graph = smallGraph();
        graph.addLocation(new Location("c", "C", "test", 2, 2));
        graph.addRoad("r2", "b", "c", 5);
        graph.setRoadOpen("r1", false);
        assertFalse(graph.getRoad(graph.indexOfRoad("r1")).isOpen());
        EdgeNode edge = graph.getAdjacencyHead(graph.indexOfLocation("a"));
        assertFalse(graph.isOpenEdge(edge));
        graph.setRoadOpen("r1", true);
        assertTrue(graph.isOpenEdge(graph.getAdjacencyHead(graph.indexOfLocation("a"))));

        graph.removeRoad("r1");
        assertEquals(1, graph.listRoads().size());
        assertThrows(ApiException.class, () -> graph.indexOfRoad("r1"));
        assertEquals("c", graph.getLocation(graph.getAdjacencyHead(graph.indexOfLocation("b")).getDestinationIndex()).getId());
    }

    @Test
    void rejectsBlankLocation() {
        Graph graph = new Graph();
        assertThrows(ApiException.class, () -> graph.addLocation(new Location(" ", "A", "t", 0, 0)));
        assertThrows(ApiException.class, () -> graph.addLocation(new Location("a", " ", "t", 0, 0)));
    }

    private Graph smallGraph() {
        Graph graph = new Graph();
        graph.addLocation(new Location("a", "A", "test", 0, 0));
        graph.addLocation(new Location("b", "B", "test", 1, 0));
        graph.addRoad("r1", "a", "b", 3);
        return graph;
    }
}
