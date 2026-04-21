package com.analytics.trainer.controller;

import com.analytics.trainer.service.ProgressService;
import com.analytics.trainer.dao.AttemptDao;
import com.analytics.trainer.util.JwtUtil;
import io.javalin.http.Context;
import java.util.Map;

public class ProgressController {
    private final ProgressService progressService;
    private final AttemptDao attemptDao;
    
    public ProgressController(ProgressService progressService, AttemptDao attemptDao) {
        this.progressService = progressService;
        this.attemptDao = attemptDao;
    }
    
    private int getUserIdFromToken(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid token");
        }
        String token = authHeader.substring(7);
        return JwtUtil.getUserIdFromToken(token);
    }
    
    public void getProgress(Context ctx) {
        try {
            int userId = getUserIdFromToken(ctx);
            var progress = progressService.getProgress(userId);
            ctx.json(Map.of(
                "total_points", progress.totalPoints(),
                "tasks_completed", progress.tasksCompleted()
            ));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
    
    public void getAttempts(Context ctx) {
        try {
            int userId = getUserIdFromToken(ctx);
            var attempts = progressService.getUserAttempts(userId);
            // Временно возвращаем пустой массив, чтобы избежать ошибки
            ctx.json(attempts.stream().map(a -> Map.of(
                "id", a.id(),
                "taskId", a.taskId(),
                "score", a.score()
            )).toList());
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }
}
