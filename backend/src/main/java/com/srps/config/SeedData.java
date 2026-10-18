package com.srps.config;

import com.srps.service.GraphService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Deterministic 10-location schematic network. Distances and coordinates are
 * demonstration data, not real geography.
 *
 * <p>College → Airport has a long direct road (1 hop, 40 km) and a cheaper
 * multi-hop path so Dijkstra and BFS minimum-hops disagree. Warehouse is
 * omitted; Restaurant is only attached through Hotel so closing that road
 * disconnects it.
 */
@Component
public class SeedData implements CommandLineRunner {

    private final GraphService graphService;

    public SeedData(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public void run(String... args) {
        graphService.seedLocation("college", "College", "campus", 120, 420);
        graphService.seedLocation("station", "Station", "transit", 280, 280);
        graphService.seedLocation("mall", "Mall", "commercial", 480, 180);
        graphService.seedLocation("hospital", "Hospital", "medical", 500, 420);
        graphService.seedLocation("bus_stop", "Bus Stop", "transit", 280, 540);
        graphService.seedLocation("park", "Park", "park", 700, 300);
        graphService.seedLocation("hotel", "Hotel", "lodging", 680, 520);
        graphService.seedLocation("airport", "Airport", "airport", 900, 200);
        graphService.seedLocation("library", "Library", "library", 140, 180);
        graphService.seedLocation("restaurant", "Restaurant", "dining", 860, 480);

        graphService.seedRoad("r1", "college", "station", 3.0);
        graphService.seedRoad("r2", "college", "bus_stop", 2.5);
        graphService.seedRoad("r3", "college", "library", 2.0);
        graphService.seedRoad("r4", "station", "mall", 4.0);
        graphService.seedRoad("r5", "station", "hospital", 4.0);
        graphService.seedRoad("r6", "bus_stop", "hospital", 3.5);
        graphService.seedRoad("r7", "mall", "park", 6.0);
        graphService.seedRoad("r8", "hospital", "park", 3.0);
        graphService.seedRoad("r9", "hospital", "hotel", 4.5);
        graphService.seedRoad("r10", "park", "airport", 5.0);
        graphService.seedRoad("r11", "hotel", "airport", 8.0);
        graphService.seedRoad("r12", "hotel", "restaurant", 2.0);
        graphService.seedRoad("r13", "library", "mall", 5.5);
        graphService.seedRoad("r14", "college", "airport", 40.0);
        graphService.seedRoad("r15", "mall", "airport", 18.0);
        graphService.seedRoad("r16", "bus_stop", "hotel", 9.0);
    }
}
