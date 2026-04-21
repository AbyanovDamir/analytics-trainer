package com.analytics.trainer;

import com.analytics.trainer.config.DatabaseConfig;
import com.analytics.trainer.controller.AuthController;
import com.analytics.trainer.controller.TaskController;
import com.analytics.trainer.controller.ProgressController;
import com.analytics.trainer.service.*;
import com.analytics.trainer.dao.*;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
    private static final int PORT = 8080;

    public static void main(String[] args) {
        logger.info("Запуск аналитического тренажёра (Java 21)...");
        
        DatabaseConfig dbConfig = new DatabaseConfig();
        UserDao userDao = new UserDao(dbConfig);
        TaskDao taskDao = new TaskDao(dbConfig);
        AttemptDao attemptDao = new AttemptDao(dbConfig);
        ProgressDao progressDao = new ProgressDao(dbConfig);
        
        AuthService authService = new AuthService(userDao);
        ScoringService scoringService = new ScoringService();
        TaskService taskService = new TaskService(taskDao, attemptDao, scoringService);
        ProgressService progressService = new ProgressService(progressDao, attemptDao);
        
        AuthController authController = new AuthController(authService);
        TaskController taskController = new TaskController(taskService, scoringService, attemptDao, progressService);
        ProgressController progressController = new ProgressController(progressService, attemptDao);
        
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(PORT);
        
        app.post("/register", authController::register);
        app.post("/login", authController::login);
        app.get("/trainings", taskController::getAllTasks);
        app.get("/trainings/{id}", taskController::getTaskById);
        app.post("/trainings/{id}/attempt", taskController::submitAttempt);
        app.get("/profile/progress", progressController::getProgress);
        // Эндпоинт истории временно отключён
        
        logger.info("Сервер запущен на порту {}", PORT);
    }
}
