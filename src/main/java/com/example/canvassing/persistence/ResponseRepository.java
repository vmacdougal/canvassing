package com.example.demo.persistence;

import com.example.demo.model.HouseholdResponse;
import com.example.demo.model.Question;
import com.example.demo.model.Response;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ResponseRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * upsert the response for a household. There is only one response per household,
     * so you will update or insert
     * @param response response to upsert
     * @return success
     */
    @Transactional
    public boolean addResponse(Response response) {
        Map<String, Object>  params = new HashMap<>();
        params.put("householdId", response.getHouseholdId());
        params.put("questionId", response.getQuestionId());
        params.put("answerId", response.getAnswerId());
        List<Response> storedResponse = jdbcTemplate.query("SELECT * FROM response WHERE household_id = :householdId AND question_id = :questionId", params, new ResponseMapper());
        if  (storedResponse.isEmpty()) {
            int rows = jdbcTemplate.update("INSERT INTO response (household_id, question_id, answer_id) VALUES (:householdId, :questionId, :answerId)", params);
            return rows > 0;
        }
        else {
            int rows = jdbcTemplate.update("UPDATE response SET answer_id = :answerId WHERE household_id = :householdId AND question_id = :questionId", params);
            return rows > 0;
        }
    }

    @Transactional
    public boolean addResponses(@NonNull List<Response> responses) {
        boolean success = true;
        for (Response response : responses) {
            success &= addResponse(response);
        }
        return success;
    }

    public List<Response> getResponses(long householdId) {
        Map<String, Object> params = Collections.singletonMap("householdId", householdId);
        String sql = """
SELECT * from response
JOIN question ON response.question_id = question.id
JOIN answer ON response.answer_id = answer.id
WHERE response.household_id = :householdId
                """;
        return jdbcTemplate.query(sql, params, new ResponseMapper());
    }
}
