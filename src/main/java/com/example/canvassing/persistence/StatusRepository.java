package com.example.canvassing.persistence;

import com.example.canvassing.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StatusRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void addStatuses() {
        Map<String, Object> params = new HashMap<>();
        for (Status status: Status.values()) {
            params.put("id", status.ordinal());
            params.put("status", status.name());
            jdbcTemplate.update("INSERT INTO status (id, status) VALUES (:id, :status)", params);
        }
    }
}
