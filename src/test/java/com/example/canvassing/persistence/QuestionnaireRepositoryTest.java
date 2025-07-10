package com.example.canvassing.persistence;

import com.example.canvassing.model.Answer;
import com.example.canvassing.model.Question;
import com.example.canvassing.model.Questionnaire;
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
@ContextConfiguration(classes = QuestionnaireRepositoryTest.TestConfig.class)
public class QuestionnaireRepositoryTest {
  @Autowired
  private QuestionnaireRepository questionRepo;

  @BeforeAll
  static void beforeAll() {
    TestConfig.postgres.start();
  }
  
  @AfterAll
  static void afterAll() {
    TestConfig.postgres.stop();
  }

  @Test
  void getQuestions() {
    //given a questionnaire
    Questionnaire questionnaire = createQuestionnaire();
    questionRepo.createQuestionnaire(questionnaire);
    //when retrieving the questions
    questionnaire = questionRepo.getQuestions();

    //then they should look as expected
    Map<Question, List<Answer>> items = questionnaire.getItems();
    assertEquals(2, items.size());
    Set<Question> questions = items.keySet();
    Question first = questions.iterator().next();
    assertEquals("Are you registered to vote?", first.getText());
    List<Answer> answers = items.get(first);
    assertEquals(2, answers.size());
    for (Answer answer: answers) {
        assertEquals(answer.getQuestionId(), first.getId());
    }
  }

  @Test
  void updateQuestions() {
    //given the initial questions
    Questionnaire questionnaire = questionRepo.getQuestions();
    //when updating with a new item and retrieving again
    List<String> answers = List.of("Yes", "No");
    questionnaire.addItem("May we contact you with reminders?", answers);
    boolean success = questionRepo.createQuestionnaire(questionnaire);
    assertTrue(success);
    questionnaire = questionRepo.getQuestions();


    //then they should look as expected
    Map<Question, List<Answer>> items = questionnaire.getItems();
    assertEquals(3, items.size());
    Set<Question> questions = items.keySet();
    Question first = questions.iterator().next();
    assertEquals("Are you registered to vote?", first.getText());
    List<Answer> firstAnswers = items.get(first);
    assertEquals(2, firstAnswers.size());
    for (Answer answer: firstAnswers) {
        assertEquals(answer.getQuestionId(), first.getId());
    }
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
