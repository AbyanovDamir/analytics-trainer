package com.analytics.trainer.dao;

import com.analytics.trainer.config.DatabaseConfig;
import com.analytics.trainer.model.User;
import java.sql.*;

public class UserDao {
    private final DatabaseConfig dbConfig;
    
    public UserDao(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
    
    public User createUser(String email, String passwordHash, String fullName) throws SQLException {
        String sql = "INSERT INTO users (email, password_hash, full_name, role) VALUES (?, ?, ?, 'student') RETURNING id";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.setString(3, fullName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), email, passwordHash, fullName, "student");
            }
            return null;
        }
    }
    
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, email, password_hash, full_name, role FROM users WHERE email = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
            }
            return null;
        }
    }
    
    public User findById(int id) throws SQLException {
        String sql = "SELECT id, email, password_hash, full_name, role FROM users WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
            }
            return null;
        }
    }
}
