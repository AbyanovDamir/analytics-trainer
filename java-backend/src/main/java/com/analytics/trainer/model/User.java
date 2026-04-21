package com.analytics.trainer.model;
public record User(int id, String email, String passwordHash, String fullName, String role) {}
