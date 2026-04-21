package com.analytics.trainer.dao;

import com.analytics.trainer.config.DatabaseConfig;
import com.analytics.trainer.model.Progress;
import java.sql.*;

public class ProgressDao {
    private final DatabaseConfig dbConfig;
    
    public ProgressDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    
    public void updateProgress(int userId) throws SQLException {
        String sql = """
            INSERT INTO user_progress (user_id, total_points, tasks_completed, last_updated)
            VALUES (?, 
                (SELECT COALESCE(SUM(score), 0) FROM attempts WHERE user_id = ?),
                (SELECT COUNT(DISTINCT task_id) FROM attempts WHERE user_id = ?),
                CURRENT_TIMESTAMP)
            ON CONFLICT (user_id) DO UPDATE SET
                total_points = (SELECT COALESCE(SUM(score), 0) FROM attempts WHERE user_id = ?),
                tasks_completed = (SELECT COUNT(DISTINCT task_id) FROM attempts WHERE user_id = ?),
                last_updated = CURRENT_TIMESTAMP
            """;
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
    }
    
    public Progress getProgress(int userId) throws SQLException {
        String sql = "SELECT user_id, total_points, tasks_completed FROM user_progress WHERE user_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Progress(
                    rs.getInt("user_id"),
                    rs.getInt("total_points"),
                    rs.getInt("tasks_completed")
                );
            }
            return new Progress(userId, 0, 0);
        }
    }
}
