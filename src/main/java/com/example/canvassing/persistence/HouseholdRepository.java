package com.example.canvassing.persistence;

import com.example.canvassing.exception.DuplicateAddressException;
import com.example.canvassing.exception.HouseholdNotFoundException;
import com.example.canvassing.model.Household;
import com.example.canvassing.model.Location;
import com.example.canvassing.model.Status;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HouseholdRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * returns all uncanvassed households within .1 degree latitude and longitude
     * of the location. Outside the polar regions, this is a rectangle about
     * 7 miles by 4-7 miles
     * @param location canvasser's location
     * @return a list of households near them
     */
    public List<Household> getUncanvassedHouseholds(Location location) {
        double minLat = location.getLatitude() - .1;
        double maxLat = location.getLatitude() + .1;
        double minLon = location.getLongitude() - .1;
        double maxLon = location.getLongitude() + .1;
        Map<String, Object>  params = new HashMap<>();
        params.put("status", Status.UNCANVASSED.name());
        params.put("minLat", minLat);
        params.put("maxLat", maxLat);
        params.put("minLon", minLon);
        params.put("maxLon", maxLon);
        String sql = """
                SELECT * FROM household WHERE household_status = (CAST(:status AS status))
                AND latitude >= :minLat AND latitude <= :maxLat
                AND longitude >= :minLon AND longitude <= :maxLon
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
        params.put("address", household.getAddress());
        params.put("latitude", household.getLatitude());
        params.put("longitude", household.getLongitude());
        params.put("status",  Status.UNCANVASSED.name());
        try {
            int rows = jdbcTemplate.update("INSERT INTO household (address, latitude, longitude, household_status) VALUES (:address, :latitude, :longitude,  (CAST(:status AS status)))",
                    params);
            return rows > 0;
        }
        catch (RuntimeException e) {
            //throw new DuplicateAddressException("This household already exists", e);
            return false;
        }
    }

    public boolean removeHousehold(long id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        int rows = jdbcTemplate.update("DELETE FROM household WHERE id = :id", params);
        return rows > 0;
    }
}
