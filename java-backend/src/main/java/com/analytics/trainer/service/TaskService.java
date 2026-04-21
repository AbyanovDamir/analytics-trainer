package com.analytics.trainer.service;

import com.analytics.trainer.dao.TaskDao;
import com.analytics.trainer.dao.AttemptDao;
import com.analytics.trainer.model.Task;
import com.fasterxml.jackson.databind.JsonNode;
import java.sql.SQLException;
import java.util.List;

public class TaskService {
    private final TaskDao taskDao;
    private final AttemptDao attemptDao;
    private final ScoringService scoringService;
    
    public TaskService(TaskDao taskDao, AttemptDao attemptDao, ScoringService scoringService) {
        this.taskDao = taskDao;
        this.attemptDao = attemptDao;
        this.scoringService = scoringService;
    }
    
    public List<Task> getAllTasks() throws SQLException {
        return taskDao.getAllTasks();
    }
    
    public Task getTaskById(int id) throws SQLException {
        return taskDao.getTaskById(id);
    }
    
    public int submitAttempt(int userId, int taskId, JsonNode answer) throws Exception {
        Task task = taskDao.getTaskById(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        
        int score;
        if ("test".equals(task.type())) {
            score = scoringService.checkTest(task.content(), answer);
        } else if ("error_spotting".equals(task.type())) {
            score = scoringService.checkErrorSpotting(task.content(), answer);
        } else {
            score = 0;
        }
        
        attemptDao.saveAttempt(userId, taskId, answer, score);
        
        return score;
    }
}
