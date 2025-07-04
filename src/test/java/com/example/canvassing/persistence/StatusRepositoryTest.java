package com.example.canvassing.persistence;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StatusRepositoryTest.TestConfig2.class)
public class StatusRepositoryTest {

  @Autowired
  private StatusRepository statusRepo;

  @BeforeAll
  static void beforeAll() {
    TestConfig2.postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    TestConfig2.postgres.stop();
  }

  @Test
  void addStatuses() {
    statusRepo.addStatuses();
  }
  static class TestConfig2 {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  private DataSource dataSource;

  @Bean
  public DataSource dataSource() {
    System.out.println("CREATNG A NEW DATASOURCE!!! XXXXXXXXXXXXXX" + dataSource);
    if (dataSource != null) {
      return dataSource;
    }
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(postgres.getJdbcUrl());
    config.setUsername(postgres.getUsername());
    config.setDriverClassName("org.postgresql.Driver");
    config.setPassword(postgres.getPassword());
    config.setMaxLifetime(5000);
    dataSource = new HikariDataSource(config);
    return dataSource;
  }

  @Bean
  public NamedParameterJdbcTemplate jdbcTemplate() {
    NamedParameterJdbcTemplate x = new NamedParameterJdbcTemplate(dataSource());
    return x;
  }
  @Bean
  public StatusRepository statusRepository() {
    return new StatusRepository();
  }

  @Bean
  public HouseholdRepository householdRepository() {
    return new HouseholdRepository();
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
}
