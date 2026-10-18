package com.srps.algorithms;

import com.srps.graph.Graph;
import com.srps.model.Location;

final class AlgorithmFixtures {

    private AlgorithmFixtures() {
    }

    /**
     * Weighted diamond used in lectures:
     * A --1-- B --100-- D
     * A --10-- C --1--- D
     * plus a cycle B --1-- C
     */
    static Graph diamond() {
        Graph graph = new Graph();
        graph.addLocation(new Location("a", "A", "t", 0, 0));
        graph.addLocation(new Location("b", "B", "t", 1, 0));
        graph.addLocation(new Location("c", "C", "t", 0, 1));
        graph.addLocation(new Location("d", "D", "t", 1, 1));
        graph.addLocation(new Location("z", "Z", "t", 5, 5));
        graph.addRoad("ab", "a", "b", 1);
        graph.addRoad("ac", "a", "c", 10);
        graph.addRoad("bd", "b", "d", 100);
        graph.addRoad("cd", "c", "d", 1);
        graph.addRoad("bc", "b", "c", 1);
        return graph;
    }

    static Graph demoSlice() {
        Graph graph = new Graph();
        graph.addLocation(new Location("college", "College", "campus", 0, 0));
        graph.addLocation(new Location("bus_stop", "Bus Stop", "transit", 1, 0));
        graph.addLocation(new Location("hospital", "Hospital", "medical", 2, 0));
        graph.addLocation(new Location("park", "Park", "park", 3, 0));
        graph.addLocation(new Location("airport", "Airport", "airport", 4, 0));
        graph.addRoad("r2", "college", "bus_stop", 2.5);
        graph.addRoad("r6", "bus_stop", "hospital", 3.5);
        graph.addRoad("r8", "hospital", "park", 3.0);
        graph.addRoad("r10", "park", "airport", 5.0);
        graph.addRoad("r14", "college", "airport", 40.0);
        return graph;
    }
}
