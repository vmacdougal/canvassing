package com.example.canvassing.persistence;

import com.example.canvassing.model.Response;
import com.example.canvassing.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class StatusRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void addStatuses() {
        Map<String, Object> params = new HashMap<>();
        for (Status status: Status.values()) {
            String statusStr = getStatus(status.ordinal());
            if (statusStr != null) {
                continue;
            }
            params.put("id", status.ordinal());
            params.put("status", status.name());
            jdbcTemplate.update("INSERT INTO status (id, status) VALUES (:id, :status)", params);
        }
    }
    private String getStatus(int id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        String sql = """
SELECT status from status
WHERE id = :id LIMIT 1
                """;
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
