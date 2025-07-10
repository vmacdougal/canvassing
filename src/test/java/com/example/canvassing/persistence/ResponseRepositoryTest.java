package com.example.canvassing.persistence;

import com.example.canvassing.model.Answer;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Question;
import com.example.canvassing.model.Questionnaire;
import com.example.canvassing.model.Response;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ResponseRepositoryTest.TestConfig.class)
public class ResponseRepositoryTest {
  @Autowired
  private ResponseRepository responseRepo;
  @Autowired
  private HouseholdRepository householdRepo;
  @Autowired
  private QuestionnaireRepository questionnaireRepo;

  @BeforeAll
  static void beforeAll() {
    TestConfig.postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    TestConfig.postgres.stop();
  }

  @Test
  void addAndRetrieveResponse() {
    //given a household
    Location location = new Location(30.0, 60.0);
    Household household = new Household("456 Elm St", location);
    householdRepo.addHousehold(household);
    household = householdRepo.getUncanvassedHouseholds(location).get(0);
    //and given a questionnaire
    Questionnaire questionnaire = createQuestionnaire();
    questionnaireRepo.createQuestionnaire(questionnaire);
    questionnaire = questionnaireRepo.getQuestions();
    Question question = questionnaire.getItems().keySet().iterator().next();
    Answer answer = questionnaire.getItems().get(question).get(0);

    //when responses are added and retrieved
    Response response = new Response();
    response.setHouseholdId(household.getId());
    response.setQuestionId(question.getId());
    response.setAnswerId(answer.getId());
    responseRepo.addResponse(response);
    List<Response> responses = responseRepo.getResponses(household.getId());

    //then it should be there
    assertEquals(1, responses.size());
    assertEquals(question.getId(), responses.get(0).getQuestionId());
    assertEquals(answer.getId(), responses.get(0).getAnswerId());
  }

private Questionnaire createQuestionnaire() {
    Questionnaire questionnaire = new Questionnaire();
    List<String> answer1 = List.of("Yes", "No");
    questionnaire.addItem("Are you registered to vote?", answer1);
    List<String> answer2 = List.of("Yes", "Ineligible", "Refused", "Took mail-in");
    questionnaire.addItem("Can I register you today?", answer2);
    return questionnaire;
  }
  static class TestConfig {
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
    config.setMaxLifetime(5000);
    return new HikariDataSource(config);
  }

  @Bean
  public NamedParameterJdbcTemplate jdbcTemplate() {
    return new NamedParameterJdbcTemplate(dataSource());
  }

  @Bean
  public ResponseRepository responseRepository() {
    return new ResponseRepository();
  }

  @Bean
  public HouseholdRepository householdRepository() {
    return new HouseholdRepository();
  }
  @Bean
  public QuestionnaireRepository questionnaireRepository() {
    return new QuestionnaireRepository();
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
