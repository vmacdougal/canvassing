package com.example.canvassing.persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class StatusRepositoryTest {


  @Autowired
  private StatusRepository statusRepo;

  @BeforeAll
  static void beforeAll() {
    TestConfig.postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    TestConfig.postgres.stop();
  }

  @Test
  void addStatuses() {
    statusRepo.addStatuses();
  }

}
