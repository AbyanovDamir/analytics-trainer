package com.analytics.trainer.controller;

import com.analytics.trainer.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import java.util.Map;

public class AuthController {
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    public void register(Context ctx) {
        try {
            Map<String, String> body = mapper.readValue(ctx.body(), Map.class);
            String email = body.get("email");
            String password = body.get("password");
            String fullName = body.get("fullName");
            
            if (email == null || password == null) {
                ctx.status(400).json(Map.of("error", "Email and password are required"));
                return;
            }
            
            String token = authService.register(email, password, fullName);
            ctx.json(Map.of("token", token, "message", "Registration successful"));
        } catch (Exception e) {
            ctx.status(400).json(Map.of("error", e.getMessage()));
        }
    }
    
    public void login(Context ctx) {
        try {
            Map<String, String> body = mapper.readValue(ctx.body(), Map.class);
            String email = body.get("email");
            String password = body.get("password");
            
            if (email == null || password == null) {
                ctx.status(400).json(Map.of("error", "Email and password are required"));
                return;
            }
            
            String token = authService.login(email, password);
            ctx.json(Map.of("token", token, "message", "Login successful"));
        } catch (Exception e) {
            ctx.status(401).json(Map.of("error", e.getMessage()));
        }
    }
}
