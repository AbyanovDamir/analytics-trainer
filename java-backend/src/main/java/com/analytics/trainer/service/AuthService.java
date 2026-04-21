package com.analytics.trainer.service;

import com.analytics.trainer.dao.UserDao;
import com.analytics.trainer.model.User;
import com.analytics.trainer.util.PasswordUtil;
import com.analytics.trainer.util.JwtUtil;
import java.sql.SQLException;

public class AuthService {
    private final UserDao userDao;
    
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public String register(String email, String password, String fullName) throws SQLException {
        // Check if user exists
        if (userDao.findByEmail(email) != null) {
            throw new RuntimeException("User already exists");
        }
        
        String passwordHash = PasswordUtil.hashPassword(password);
        User user = userDao.createUser(email, passwordHash, fullName);
        return JwtUtil.generateToken(user.id(), user.email());
    }
    
    public String login(String email, String password) throws SQLException {
        User user = userDao.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        if (!PasswordUtil.checkPassword(password, user.passwordHash())) {
            throw new RuntimeException("Invalid password");
        }
        
        return JwtUtil.generateToken(user.id(), user.email());
    }
    
    public User getUserFromToken(String token) throws SQLException {
        int userId = JwtUtil.getUserIdFromToken(token);
        return userDao.findById(userId);
    }
}
