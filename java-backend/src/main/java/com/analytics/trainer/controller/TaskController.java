package com.analytics.trainer.controller;

import com.analytics.trainer.service.TaskService;
import com.analytics.trainer.service.ScoringService;
import com.analytics.trainer.dao.AttemptDao;
import com.analytics.trainer.service.ProgressService;
import com.analytics.trainer.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.Context;
import java.util.Map;

public class TaskController {
    private final TaskService taskService;
    private final ScoringService scoringService;
    private final AttemptDao attemptDao;
    private final ProgressService progressService;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public TaskController(TaskService taskService, ScoringService scoringService, 
                          AttemptDao attemptDao, ProgressService progressService) {
        this.taskService = taskService;
        this.scoringService = scoringService;
        this.attemptDao = attemptDao;
        this.progressService = progressService;
    }
    
    private int getUserIdFromToken(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid token");
        }
        String token = authHeader.substring(7);
        return JwtUtil.getUserIdFromToken(token);
    }
    
    public void getAllTasks(Context ctx) {
        try {
            var tasks = taskService.getAllTasks();
            ctx.json(tasks);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
    
    public void getTaskById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var task = taskService.getTaskById(id);
            if (task == null) {
                ctx.status(404).json(Map.of("error", "Task not found"));
                return;
            }
            ctx.json(task);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
    
    public void submitAttempt(Context ctx) {
        try {
            int userId = getUserIdFromToken(ctx);
            int taskId = Integer.parseInt(ctx.pathParam("id"));
            
            // Parse the request body
            JsonNode requestBody = mapper.readTree(ctx.body());
            JsonNode answer = requestBody.get("answer");
            
            if (answer == null) {
                ctx.status(400).json(Map.of("error", "Missing 'answer' field"));
                return;
            }
            
            int score = taskService.submitAttempt(userId, taskId, answer);
            progressService.updateProgress(userId);
            
            ctx.json(Map.of(
                "score", score,
                "max_score", 100,
                "message", "Attempt submitted successfully"
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}
