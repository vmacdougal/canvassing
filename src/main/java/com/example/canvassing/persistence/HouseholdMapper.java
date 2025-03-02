package com.example.canvassing.persistence;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HouseholdMapper implements RowMapper<Household> {
    private static final Map<Integer, Status> STATUSES = new HashMap<>();
    static {
        for (Status status : Status.values()) {
            STATUSES.put(status.ordinal(), status);
        }
    }
    public Household mapRow(ResultSet rs, int rowNum) throws SQLException {
        Location location = new Location(rs.getDouble("latitude"), rs.getDouble("longitude"));
        Household household = new Household(rs.getString("address"), location);
        household.setStatus(STATUSES.get(rs.getInt("status")));
        household.setId(rs.getLong("id"));
        return household;
    }
}
