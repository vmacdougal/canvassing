package com.example.demo.persistence;

import com.example.demo.model.Response;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResponseMapper implements RowMapper<Response> {

    @Override
    public Response mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Response(rs.getLong("id"),
                rs.getLong("household_id"),
                rs.getLong("question_id"),
                rs.getLong("answer_id"),
                rs.getString("question"),
                rs.getString("answer"));
    }
}
