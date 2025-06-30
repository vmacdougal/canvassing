package com.example.canvassing.persistence;

import com.example.canvassing.model.Answer;
import com.example.canvassing.model.Question;
import com.example.canvassing.model.Questionnaire;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class QuestionnaireRepositoryTest {
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );
  @Autowired
  private QuestionnaireRepository questionRepo;

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
  void getQuestions() {
    //when retrieving the questions
    Questionnaire questionnaire = questionRepo.getQuestions();

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
  @Transactional
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




}
