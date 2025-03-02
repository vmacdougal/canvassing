package com.example.canvassing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HouseholdResponse {
    private final Household household;
    private List<Response> responses;
}
