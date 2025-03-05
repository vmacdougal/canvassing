package com.example.canvassing.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * This is what the canvasser will work off of. Households, questions, and possible answers.
 */
@AllArgsConstructor
@Getter
public class CanvassList {
    private List<Household> households;
    private Questionnaire questionnaire;
}
