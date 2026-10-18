package com.srps.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression coverage for the road-status endpoint that produced a production
 * HTTP 500, plus the graph reset behaviour it depends on.
 *
 * <p>The graph is a shared in-memory singleton across the whole test context, so
 * every test starts from the canonical network via {@code POST /api/graph/reset}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RoadStatusApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void restoreCanonicalGraph() throws Exception {
        mockMvc.perform(post("/api/graph/reset")).andExpect(status().isOk());
    }

    @Test
    void closingAndReopeningAValidRoadReturnsUpdatedStatus() throws Exception {
        mockMvc.perform(patch("/api/roads/r14/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r14"))
                .andExpect(jsonPath("$.isOpen").value(false));

        mockMvc.perform(patch("/api/roads/r14/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOpen").value(true));
    }

    @Test
    void unknownRoadIdIsNotFound() throws Exception {
        mockMvc.perform(patch("/api/roads/r-does-not-exist/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":false}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    /**
     * The exact reported failure: a GET on the PATCH-only status path must not be
     * reported as a server error.
     */
    @Test
    void getOnStatusEndpointIsMethodNotAllowedNotServerError() throws Exception {
        mockMvc.perform(get("/api/roads/r14/status"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void malformedStatusBodyIsBadRequest() throws Exception {
        mockMvc.perform(patch("/api/roads/r14/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\": "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unmappedPathIsNotFoundNotServerError() throws Exception {
        mockMvc.perform(get("/api/no-such-endpoint"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void resetRestoresCanonicalRoadsAndShortestRouteStillResolves() throws Exception {
        mockMvc.perform(delete("/api/roads/r14")).andExpect(status().isNoContent());
        assertEquals(15, roadCount(getGraph()));

        // A road the map no longer shows cannot be edited: this is the drift the
        // frontend's mount-time reset prevents.
        mockMvc.perform(patch("/api/roads/r14/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":false}"))
                .andExpect(status().isNotFound());

        JsonNode reset = json(mockMvc.perform(post("/api/graph/reset")).andExpect(status().isOk()).andReturn());
        assertEquals(10, reset.get("locations").size());
        assertEquals(16, reset.get("roads").size());

        JsonNode shortest = json(mockMvc.perform(
                get("/api/routes/shortest?sourceId=college&destinationId=airport")).andReturn());
        assertTrue(shortest.get("reachable").asBoolean());
        assertEquals(14.0, shortest.get("totalDistanceKm").asDouble());
    }

    private JsonNode getGraph() throws Exception {
        return json(mockMvc.perform(get("/api/graph")).andExpect(status().isOk()).andReturn());
    }

    private int roadCount(JsonNode graph) {
        return graph.get("roads").size();
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
