package com.example.canvassing.persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StatusRepositoryTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );
  @Autowired
  private StatusRepository statusRepo;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Test
  void addStatuses() {
    statusRepo.addStatuses();
  }
}
