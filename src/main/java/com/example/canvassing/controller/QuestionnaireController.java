package com.example.canvassing.controller;

import com.example.canvassing.model.Questionnaire;
import com.example.canvassing.service.HouseholdService;
import com.example.canvassing.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionnaireController {
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private HouseholdService householdService;

    @GetMapping("/questionnaire")
    public ResponseEntity<Questionnaire> getQuestionnaire() {
        return new ResponseEntity<>(questionnaireService.getQuestionnaire(), HttpStatus.OK);
    }

    /**
     * Change the canvassing questionnaire. This will wipe out any canvass results and
     * set all households back to UNCANVASSED
     * @param questionnaire the new questionnaire
     * @return success if it succeeds
     */
    @PostMapping("/questionnaire")
    public ResponseEntity<Boolean> addQuestionnaire(@RequestBody Questionnaire questionnaire) {
        boolean success = questionnaireService.addQuestionnaire(questionnaire);
        householdService.resetStatus();
        return new ResponseEntity<>(success, HttpStatus.OK);
    }
}
