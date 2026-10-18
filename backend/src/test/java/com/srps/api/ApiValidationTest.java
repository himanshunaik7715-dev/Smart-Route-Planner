package com.srps.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Focused checks for request validation, error status codes, and CORS behaviour.
 * Every case here is non-mutating so the seeded graph is untouched.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiValidationTest {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthReportsUp() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void missingRequiredQueryParameterIsBadRequest() throws Exception {
        mockMvc.perform(get("/api/routes/shortest?sourceId=college"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/routes/min-hops"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownLocationIsNotFound() throws Exception {
        mockMvc.perform(get("/api/routes/shortest?sourceId=college&destinationId=does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("does-not-exist")));
    }

    @Test
    void blankLocationNameIsRejected() throws Exception {
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"type\":\"other\",\"x\":10,\"y\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void duplicateLocationIdIsConflict() throws Exception {
        mockMvc.perform(post("/api/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Duplicate College\",\"type\":\"other\",\"x\":1,\"y\":1,\"id\":\"college\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("college")));
    }

    @Test
    void unknownRoadStatusUpdateIsNotFound() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/roads/no-such-road/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isOpen\":false}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void corsAllowsOnlyTheConfiguredOrigin() throws Exception {
        mockMvc.perform(options("/api/graph")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));

        mockMvc.perform(options("/api/graph")
                        .header(HttpHeaders.ORIGIN, "https://not-allowed.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void errorResponsesUseAConsistentShape() throws Exception {
        mockMvc.perform(get("/api/routes/shortest?sourceId=missing&destinationId=college"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());
    }
}
