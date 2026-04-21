package com.analytics.trainer.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private final HikariDataSource dataSource;

    public DatabaseConfig() {
        HikariConfig config = new HikariConfig();
        String dbUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/trainer");
        config.setJdbcUrl(dbUrl);
        config.setUsername(System.getenv().getOrDefault("DB_USER", "admin"));
        config.setPassword(System.getenv().getOrDefault("DB_PASSWORD", "secret"));
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
