package com.example.canvassing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HouseholdUpdate {
    private final Household household;
    //false indicates the household was deleted
    private final boolean created;
}
