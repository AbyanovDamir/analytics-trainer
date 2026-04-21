package com.analytics.trainer.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record Attempt(
    int id, 
    int userId, 
    int taskId, 
    JsonNode answer, 
    Integer score,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime completedAt
) {}
