package com.analytics.trainer.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public record Attempt(
    int id, 
    int userId, 
    int taskId, 
    JsonNode answer, 
    Integer score,
    @JsonIgnore
    LocalDateTime completedAt
) {}
