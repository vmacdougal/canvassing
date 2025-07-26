package com.example.canvassing.persistence;

import com.example.canvassing.exception.BadLocationException;
import com.example.canvassing.exception.DuplicateAddressException;
import com.example.canvassing.exception.HouseholdNotFoundException;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import lombok.NonNull;
import net.postgis.jdbc.PGgeometry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HouseholdRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private static final String POINT_FORMAT = "SRID=4326;POINT(%s %s)";

    /**
     * returns all uncanvassed households within .1 degree latitude and longitude
     * of the location. Outside the polar regions, this is a rectangle about
     * 7 miles by 4-7 miles
     * @param location canvasser's location
     * @return a list of households near them
     */
    public List<Household> getUncanvassedHouseholds(Location location) {
        PGgeometry origin = getGeometry(location.getLatitude(), location.getLongitude());
        Map<String, Object>  params = new HashMap<>();
        params.put("status", Status.UNCANVASSED.name());
        params.put("location", origin);
        String sql = """
                SELECT *, household.location_geo <-> :location AS distance
                FROM household WHERE household_status = (CAST(:status AS status))
                ORDER BY distance
                LIMIT 100
                """;
        return jdbcTemplate.query(sql, params, new HouseholdMapper());
    }

    public List<Household> getCanvassedHouseholds() {
        Map<String, Object>  params = new HashMap<>();
        params.put("status", Status.UNCANVASSED.name());
        return jdbcTemplate.query("SELECT * FROM household WHERE household_status != (CAST(:status AS status))", params, new HouseholdMapper());
    }

    public Household getHousehold(long id) {
        Map<String, Object>  params = Collections.singletonMap("id", id);
        return jdbcTemplate.query("SELECT * FROM household WHERE id = :id", params, new HouseholdMapper()).stream().findFirst().orElse(null);
    }

    public boolean setStatus(@NonNull Household household) {
        Map<String, Object>  params = new HashMap<>();
        params.put("id", household.getId());
        List<Household> households = jdbcTemplate.query("SELECT * FROM household WHERE id = :id", params, new HouseholdMapper());
        if (households.isEmpty()) {
            throw new HouseholdNotFoundException("No household found with id " + household.getId());
        }
        params.put("status", household.getStatus().name());
        int rows = jdbcTemplate.update("UPDATE household SET household_status = (CAST(:status AS status)) WHERE id = :id", params);
        return rows > 0;
    }

    public boolean addHousehold(@NonNull Household household) {
        Map<String, Object> params = new HashMap<>();
        PGgeometry location = getGeometry(household.getLatitude(), household.getLongitude());
        params.put("address", household.getAddress());
        params.put("latitude", household.getLatitude());
        params.put("longitude", household.getLongitude());
        params.put("status",  Status.UNCANVASSED.name());
        params.put("location", location);
        try {
            int rows = jdbcTemplate.update("INSERT INTO household (address, latitude, longitude, household_status, location_geo) VALUES (:address, :latitude, :longitude,  (CAST(:status AS status)), :location)",
                    params);
            return rows > 0;
        }
        catch (DuplicateKeyException e) {
            //throw new DuplicateAddressException("This household already exists", e);
            return false;
        }
        catch (RuntimeException e) {
            throw new BadLocationException("household " + household + " does not have a valid latitude/longitude", e);
        }
    }

    public boolean removeHousehold(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        int rows = jdbcTemplate.update("DELETE FROM household WHERE id = :id", params);
        return rows > 0;
    }

    private PGgeometry getGeometry(double latitude, double longitude) {
        PGgeometry location = null;
        try {
            String latString = Double.toString(latitude);
            String lonString = Double.toString(longitude);
            location = new PGgeometry(String.format(POINT_FORMAT, lonString, latString));
        }
        catch (SQLException sqlException) {
            throw new BadLocationException("household " + latitude + " " + longitude + " does not have a valid latitude/longitude", sqlException);
        }
        return location;
    }
}
