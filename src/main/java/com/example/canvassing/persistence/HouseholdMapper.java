package com.example.canvassing.persistence;

import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;

import net.postgis.jdbc.PGgeometry;
import net.postgis.jdbc.geometry.Point;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HouseholdMapper implements RowMapper<Household> {
    public Household mapRow(ResultSet rs, int rowNum) throws SQLException {
        PGgeometry geometry = (PGgeometry) rs.getObject("location_geo");
        Point point = geometry.getGeometry().getFirstPoint();
        Location location = new Location(point.y, point.x);
        Household household = new Household(rs.getString("address"), location);
        String status = rs.getString("household_status");
        household.setStatus(Status.valueOf(status));
        household.setId(rs.getLong("id"));
        return household;
    }
}
