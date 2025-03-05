package com.example.canvassing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * a household which was either created or deleted. For the web socket
 * to update the front ends.
 */
@Getter
@AllArgsConstructor
public class HouseholdUpdate {
    private final Household household;
    //false indicates the household was deleted
    private final boolean created;
}
