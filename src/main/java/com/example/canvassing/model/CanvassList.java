package com.example.canvassing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CanvassList {
    private List<Household> households;
    private Questionnaire questionnaire;
}
