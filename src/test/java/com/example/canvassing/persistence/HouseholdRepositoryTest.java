package com.example.canvassing.persistence;

import com.example.canvassing.exception.BadLocationException;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import java.util.List;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = HouseholdRepositoryTest.TestConfig.class)
public class HouseholdRepositoryTest {

  @Autowired
  private HouseholdRepository householdRepository;

  @BeforeAll
  static void beforeAll() {
    TestConfig.postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    TestConfig.postgres.stop();
  }

  @BeforeEach
  void beforeEach() {
  }

  @AfterEach
  void afterEach() {
  }

  @Test
  void addHousehold() {
    //given a household
    Location location = new Location(30.0, 60.0);
    Household household = new Household("123 Elm St", location);

    //when adding it
    boolean success = householdRepository.addHousehold(household);
    assertTrue(success);

    //then it should be found
    List<Household> uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    Household found = findHousehold(uncanvassed, "123 Elm St");
    assertNotNull(found);
  }

  @Test
  void addAndRemoveHousehold() {
    //given a household
    Location location = new Location(31.0, 61.0);
    Household household = new Household("456 Elm St", location);

    //when adding it
    boolean success = householdRepository.addHousehold(household);
    assertTrue(success);

    List<Household> uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    Household retrievedHousehold = findHousehold(uncanvassed, "456 Elm St");
    assertNotNull(retrievedHousehold);
    
    //and when removing it
    success = householdRepository.removeHousehold(retrievedHousehold.getId());
    assertTrue(success);

    //then it should not be there
    uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    retrievedHousehold = findHousehold(uncanvassed, "456 Elm St");
    assertNull(retrievedHousehold);
  }

  @Test
  void canvassHousehold() {
        //given a household
    Location location = new Location(32.0, 62.0);
    Household household = new Household("789 Elm St", location);

    //when adding it
    boolean success = householdRepository.addHousehold(household);
    assertTrue(success);
    List<Household> uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    Household retrievedHousehold = findHousehold(uncanvassed, "789 Elm St");

    //and canvassing it
    retrievedHousehold.setStatus(Status.CANVASSED);
    success = householdRepository.setStatus(retrievedHousehold);

    //then it should be successful
    assertTrue(success);
    List<Household> canvassed = householdRepository.getCanvassedHouseholds();
    retrievedHousehold = findHousehold(canvassed, "789 Elm St");
    assertNotNull(retrievedHousehold);
  }

  @Test
  void invalidLocation() {
    //given a household with an invalid lat/lon
    Location location = new Location(932.0, 962.0);
    Household household = new Household("1 Evergreen Terrace", location);

    //when adding it, it should throw an exception
    assertThrows(BadLocationException.class, () -> {householdRepository.addHousehold(household);
    });
  }

  private Household findHousehold(List<Household> households, String address) {
    for (Household retrieved: households) {
        if (retrieved.getAddress().equals(address)) {
            return retrieved;
        }
    }
    return null;
}
  static class TestConfig {
    static DockerImageName myImage = DockerImageName.parse("postgis/postgis").asCompatibleSubstituteFor("postgres");
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    myImage
  );

  private DataSource dataSource;

  @Bean
  public DataSource dataSource() {
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
    return new NamedParameterJdbcTemplate(dataSource());
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
