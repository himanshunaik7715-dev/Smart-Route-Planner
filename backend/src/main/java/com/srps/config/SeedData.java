package com.srps.config;

import com.srps.service.GraphService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the deterministic 10-location schematic network on startup. The same
 * canonical network is re-applied on demand via {@link GraphService#resetToDefault()}.
 */
@Component
public class SeedData implements CommandLineRunner {

    private final GraphService graphService;

    public SeedData(GraphService graphService) {
        this.graphService = graphService;
    }

    @Override
    public void run(String... args) {
        graphService.resetToDefault();
    }
}
