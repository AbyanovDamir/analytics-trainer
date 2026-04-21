package com.analytics.trainer.dao;

import com.analytics.trainer.config.DatabaseConfig;
import com.analytics.trainer.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
    private final DatabaseConfig dbConfig;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public TaskDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    
    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, type, title, description, content, max_score FROM tasks ORDER BY id";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                try {
                    tasks.add(new Task(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getString("description"),
                        mapper.readTree(rs.getString("content")),
                        rs.getInt("max_score")
                    ));
                } catch (Exception e) {
                    throw new SQLException(e);
                }
            }
        }
        return tasks;
    }
    
    public Task getTaskById(int id) throws SQLException {
        String sql = "SELECT id, type, title, description, content, max_score FROM tasks WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                try {
                    return new Task(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getString("description"),
                        mapper.readTree(rs.getString("content")),
                        rs.getInt("max_score")
                    );
                } catch (Exception e) {
                    throw new SQLException(e);
                }
            }
            return null;
        }
    }
}
