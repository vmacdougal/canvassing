package com.example.canvassing.persistence;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class HouseholdRepositoryTest {
   static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );
  @Autowired
  private HouseholdRepository householdRepository;

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Test
  @Transactional
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
  @Transactional
  void addAndRemoveHousehold() {
    //given a household
    Location location = new Location(30.0, 60.0);
    Household household = new Household("123 Elm St", location);

    //when adding it
    boolean success = householdRepository.addHousehold(household);
    assertTrue(success);

    List<Household> uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    Household retrievedHousehold = findHousehold(uncanvassed, "123 Elm St");
    assertNotNull(retrievedHousehold);
    
    //and when removing it
    success = householdRepository.removeHousehold(retrievedHousehold.getId());
    assertTrue(success);

    //then it should not be there
    uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    retrievedHousehold = findHousehold(uncanvassed, "123 Elm St");
    assertNull(retrievedHousehold);
  }

  @Test
  @Transactional
  void canvassHousehold() {
        //given a household
    Location location = new Location(30.0, 60.0);
    Household household = new Household("123 Elm St", location);

    //when adding it
    boolean success = householdRepository.addHousehold(household);
    assertTrue(success);
    List<Household> uncanvassed = householdRepository.getUncanvassedHouseholds(location);
    Household retrievedHousehold = findHousehold(uncanvassed, "123 Elm St");

    //and canvassing it
    retrievedHousehold.setStatus(Status.CANVASSED);
    success = householdRepository.setStatus(retrievedHousehold);

    //then it should be successful
    assertTrue(success);
    List<Household> canvassed = householdRepository.getCanvassedHouseholds();
    retrievedHousehold = findHousehold(canvassed, "123 Elm St");
    assertNotNull(retrievedHousehold);
  }

  private Household findHousehold(List<Household> households, String address) {
    for (Household retrieved: households) {
        if (retrieved.getAddress().equals(address)) {
            return retrieved;
        }
    }
    return null;
}
}
