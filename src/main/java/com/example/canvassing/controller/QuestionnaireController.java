package com.example.canvassing.controller;

import com.example.canvassing.model.Questionnaire;
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

    @GetMapping("/questionnaire")
    public ResponseEntity<Questionnaire> getQuestionnaire() {
        return new ResponseEntity<>(questionnaireService.getQuestionnaire(), HttpStatus.OK);
    }

    @PostMapping("/questionnaire")
    public ResponseEntity<Boolean> addQuestionnaire(@RequestBody Questionnaire questionnaire) {
        return new ResponseEntity<>(questionnaireService.addQuestionnaire(questionnaire), HttpStatus.OK);
    }
}
