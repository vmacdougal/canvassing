package com.example.canvassing.persistence;

import com.example.canvassing.model.Answer;
import com.example.canvassing.model.Question;
import com.example.canvassing.model.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class QuestionnaireRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Questionnaire getQuestions() {
        Questionnaire questionnaire = new Questionnaire();
        List<Question> questions = jdbcTemplate.query("SELECT * FROM question ORDER BY id", new HashMap<>(), new QuestionMapper());
        for (Question question : questions) {
            Map<String, Object> questionParams = Collections.singletonMap("question_id", question.getId());
            List<Answer> answers = jdbcTemplate.query("SELECT * FROM answer WHERE question_id = :question_id ORDER BY id", questionParams, new AnswerMapper());
            questionnaire.getItems().put(question, answers);
        }
        return questionnaire;
    }

    @Transactional
    public boolean createQuestionnaire(Questionnaire questionnaire) {
        //only one questionnaire is live, so clean out any previous ones
        jdbcTemplate.update("DELETE FROM question", new  HashMap<>());
        jdbcTemplate.update("DELETE FROM answer", new  HashMap<>());
        List<Question> questions = new ArrayList<>(questionnaire.getItems().keySet());
        //insert all the
        jdbcTemplate.batchUpdate("INSERT INTO question (question) VALUES (:text)", SqlParameterSourceUtils.createBatch(questions));
        for (Question question : questions) {
            Map<String, Object> params = Collections.singletonMap("text", question.getText());
            Question storedQuestion = jdbcTemplate.query("SELECT * FROM question WHERE question = :text", params, new QuestionMapper()).stream().findFirst().orElse(null);
            if (storedQuestion == null) {
                throw new RuntimeException("Question " + question.getText() + " not found in the database; failed to update");
            }
            List<Answer> answers = questionnaire.getItems().get(question);
            for  (Answer answer : answers) {
                answer.setQuestionId(storedQuestion.getId());
            }
            jdbcTemplate.batchUpdate("INSERT INTO answer (question_id, answer) VALUES (:questionId, :text)", SqlParameterSourceUtils.createBatch(answers));

        }
        return true;

    }
}
