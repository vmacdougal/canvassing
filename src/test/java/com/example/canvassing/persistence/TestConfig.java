package com.example.canvassing.persistence;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class TestConfig {
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(postgres.getJdbcUrl());
    config.setUsername(postgres.getUsername());
    config.setDriverClassName("org.postgresql.Driver");
    config.setPassword(postgres.getPassword());
    return new HikariDataSource(config);
  }

  @Bean
  public NamedParameterJdbcTemplate jdbcTemplate() {
    return new NamedParameterJdbcTemplate(dataSource());
  }
  @Bean
  public StatusRepository statusRepository() {
    return new StatusRepository();
  }

  @Bean
  public Flyway flyway() {
    Flyway flyway = Flyway.configure()
    .dataSource(dataSource())
    .mixed(true)
    .locations("filesystem:src/main/resources")
    .load();
    flyway.migrate();
    return flyway;
  }

}
