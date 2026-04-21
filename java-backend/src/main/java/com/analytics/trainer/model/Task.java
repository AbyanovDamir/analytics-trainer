package com.analytics.trainer.model;
import com.fasterxml.jackson.databind.JsonNode;
public record Task(int id, String type, String title, String description, JsonNode content, int maxScore) {}
