package com.example.canvassing.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * a household.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Household {
    private Long id;

    private String address;
    private Status status;
    private double latitude;
    private double longitude;

    public Household(String address, Location location) {
        this.address = address;
        this.latitude =  location.getLatitude();
        this.longitude =  location.getLongitude();
        status = Status.UNCANVASSED;
    }

    @JsonIgnore
    public Location getLocation() {
        return new Location(latitude, longitude);
    }
}
