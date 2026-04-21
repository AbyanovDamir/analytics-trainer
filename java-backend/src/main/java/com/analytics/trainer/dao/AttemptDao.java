package com.analytics.trainer.dao;

import com.analytics.trainer.config.DatabaseConfig;
import com.analytics.trainer.model.Attempt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttemptDao {
    private final DatabaseConfig dbConfig;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public AttemptDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    
    public int saveAttempt(int userId, int taskId, JsonNode answer, int score) throws SQLException {
        String sql = "INSERT INTO attempts (user_id, task_id, answer, score) VALUES (?, ?, ?::jsonb, ?) RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, taskId);
            stmt.setString(3, answer.toString());
            stmt.setInt(4, score);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        }
    }
    
    public List<Attempt> getUserAttempts(int userId) throws SQLException {
        List<Attempt> attempts = new ArrayList<>();
        String sql = "SELECT id, user_id, task_id, answer, score, completed_at FROM attempts WHERE user_id = ? ORDER BY completed_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                attempts.add(new Attempt(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("task_id"),
                    mapper.readTree(rs.getString("answer")),
                    rs.getInt("score"),
                    rs.getTimestamp("completed_at").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return attempts;
    }
    
    public Integer getBestScore(int userId, int taskId) throws SQLException {
        String sql = "SELECT MAX(score) as best_score FROM attempts WHERE user_id = ? AND task_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, taskId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("best_score");
            }
            return 0;
        }
    }
}
