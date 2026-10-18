package com.srps.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoadStatusRequest(@JsonProperty("isOpen") boolean isOpen) {
}
