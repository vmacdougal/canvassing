package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Location {
    private final double latitude;
    private final double longitude;

    public double distance(Location location) {
        return Math.sqrt(Math.pow(location.latitude - this.latitude, 2) +  Math.pow(location.longitude - this.longitude, 2));
    }

    /**
     * for use in comparators.
     * @param location1
     * @param location2
     * @return 1 if location1 is closer to this location, -1 if location 2 is, 0 for a tie.
     */
    public int compare(Location location1, Location location2) {
        double distance1 = distance(location1);
        double distance2 = distance(location2);
        if (distance1 < distance2) {
            return -1;
        }
        else if (distance1 > distance2) {
            return 1;
        }
        return 0;
    }
}
