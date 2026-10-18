package com.srps.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void healthAndSeededGraphAreAvailable() throws Exception {
        mockMvc.perform(get("/api/health")).andExpect(status().isOk());
        MvcResult result = mockMvc.perform(get("/api/graph")).andExpect(status().isOk()).andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertTrue(body.get("locations").size() >= 10);
        assertTrue(body.get("roads").size() >= 16);
    }

    @Test
    void shortestAndMinHopsDisagreeOnCollegeAirport() throws Exception {
        JsonNode shortest = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=airport"));
        JsonNode hops = json(getRoute("/api/routes/min-hops?sourceId=college&destinationId=airport"));
        assertEquals("DIJKSTRA", shortest.get("algorithm").asText());
        assertEquals("BFS_MIN_HOPS", hops.get("algorithm").asText());
        assertEquals(14.0, shortest.get("totalDistanceKm").asDouble());
        assertEquals(40.0, hops.get("totalDistanceKm").asDouble());
        assertEquals(1, hops.get("roadIds").size());
        assertTrue(shortest.get("reachable").asBoolean());
    }

    @Test
    void sameNodeUnreachableAndInvalidIds() throws Exception {
        JsonNode same = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=college"));
        assertEquals(0.0, same.get("totalDistanceKm").asDouble());
        assertEquals(1, same.get("locationIds").size());

        mockMvc.perform(get("/api/routes/shortest?sourceId=missing&destinationId=college"))
                .andExpect(status().isNotFound());

        String isolatedId = "isolated-" + System.nanoTime();
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Isolated\",\"type\":\"other\",\"x\":40,\"y\":40,\"id\":\"" + isolatedId + "\"}"))
                .andExpect(status().isCreated());
        JsonNode unreachable = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=" + isolatedId));
        assertFalse(unreachable.get("reachable").asBoolean());
        assertTrue(unreachable.get("locationIds").isEmpty());
        assertTrue(unreachable.get("totalDistanceKm").isNull());
    }

    @Test
    void closingARoadChangesTheRouteAndReopeningRestoresIt() throws Exception {
        JsonNode before = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=airport"));
        String firstRoad = before.get("roadIds").get(0).asText();

        mockMvc.perform(patch("/api/roads/" + firstRoad + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":false}"))
                .andExpect(status().isOk());

        JsonNode closed = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=airport"));
        assertTrue(closed.get("reachable").asBoolean());
        assertFalse(closed.get("roadIds").toString().contains("\"" + firstRoad + "\""));

        mockMvc.perform(patch("/api/roads/" + firstRoad + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":true}"))
                .andExpect(status().isOk());

        JsonNode restored = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=airport"));
        assertEquals(before.get("totalDistanceKm").asDouble(), restored.get("totalDistanceKm").asDouble());
    }

    @Test
    void duplicateRoadAndInvalidDistanceAreRejected() throws Exception {
        mockMvc.perform(post("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromLocationId\":\"college\",\"toLocationId\":\"station\",\"distanceKm\":3}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromLocationId\":\"college\",\"toLocationId\":\"restaurant\",\"distanceKm\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bfsAndDfsVisitEachVertexOnce() throws Exception {
        JsonNode bfs = json(getRoute("/api/routes/bfs?sourceId=college"));
        JsonNode dfs = json(getRoute("/api/routes/dfs?sourceId=college"));
        assertEquals("BFS", bfs.get("algorithm").asText());
        assertEquals("DFS", dfs.get("algorithm").asText());
        assertEquals(bfs.get("visitOrder").size(), bfs.get("visitOrder").size());
        assertTrue(bfs.get("visitOrder").size() >= 9);
    }

    @Test
    void canAddAndDeleteARoad() throws Exception {
        mockMvc.perform(post("/api/roads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"r-temp\",\"fromLocationId\":\"library\",\"toLocationId\":\"bus_stop\",\"distanceKm\":7.5}"))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/api/roads/r-temp")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/roads/r-temp")).andExpect(status().isNotFound());
    }

    @Test
    void resetRestoresCanonicalGraphAndRoutesStillWork() throws Exception {
        // Establish a known baseline regardless of other tests.
        JsonNode baseline = json(mockMvc.perform(post("/api/graph/reset")).andExpect(status().isOk()).andReturn());
        assertEquals(10, baseline.get("locations").size());
        assertEquals(16, baseline.get("roads").size());

        // Mutate the shared in-memory graph: remove a road and add a location.
        mockMvc.perform(delete("/api/roads/r1")).andExpect(status().isNoContent());
        String extraId = "reset-extra-" + System.nanoTime();
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Extra\",\"type\":\"other\",\"x\":10,\"y\":10,\"id\":\"" + extraId + "\"}"))
                .andExpect(status().isCreated());

        JsonNode mutated = json(mockMvc.perform(get("/api/graph")).andExpect(status().isOk()).andReturn());
        assertEquals(11, mutated.get("locations").size());
        assertEquals(15, mutated.get("roads").size());

        // Reset restores the canonical network, so Remove/route inputs match again.
        JsonNode reset = json(mockMvc.perform(post("/api/graph/reset")).andExpect(status().isOk()).andReturn());
        assertEquals(10, reset.get("locations").size());
        assertEquals(16, reset.get("roads").size());

        // Shortest distance works on the restored graph.
        JsonNode shortest = json(getRoute("/api/routes/shortest?sourceId=college&destinationId=airport"));
        assertTrue(shortest.get("reachable").asBoolean());
        assertEquals(14.0, shortest.get("totalDistanceKm").asDouble());
    }

    private MvcResult getRoute(String url) throws Exception {
        return mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
