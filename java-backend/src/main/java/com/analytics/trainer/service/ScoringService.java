package com.analytics.trainer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoringService {
    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);
    private static final String CHECKER_URL = "http://checker:8081";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    
    public int checkTest(JsonNode taskContent, JsonNode userAnswer) {
        logger.info("=== checkTest called ===");
        logger.info("taskContent: {}", taskContent);
        logger.info("userAnswer: {}", userAnswer);
        
        try {
            ObjectNode request = mapper.createObjectNode();
            request.set("task_content", taskContent);
            request.set("user_answer", userAnswer);
            
            String requestBody = mapper.writeValueAsString(request);
            logger.info("Request body: {}", requestBody);
            logger.info("Calling checker at: {}/check/test", CHECKER_URL);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(CHECKER_URL + "/check/test"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            logger.info("Response status: {}", response.statusCode());
            logger.info("Response body: {}", response.body());
            
            JsonNode result = mapper.readTree(response.body());
            int score = result.has("score") ? result.get("score").asInt() : 0;
            logger.info("Score: {}", score);
            
            return score;
        } catch (Exception e) {
            logger.error("Error calling checker: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    public int checkErrorSpotting(JsonNode taskContent, JsonNode userAnswer) {
        logger.info("=== checkErrorSpotting called ===");
        try {
            ObjectNode request = mapper.createObjectNode();
            request.set("task_content", taskContent);
            request.set("user_answer", userAnswer);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(CHECKER_URL + "/check/error-spotting"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(request)))
                .build();
            
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode result = mapper.readTree(response.body());
            
            return result.has("score") ? result.get("score").asInt() : 0;
        } catch (Exception e) {
            logger.error("Error calling checker: {}", e.getMessage(), e);
            return 0;
        }
    }
}
