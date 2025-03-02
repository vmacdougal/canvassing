package com.example.canvassing.service;

import com.example.canvassing.model.Questionnaire;
import com.example.canvassing.persistence.QuestionnaireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class QuestionnaireService {
    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    public Questionnaire getQuestionnaire() {
        return questionnaireRepository.getQuestions();
    }

    public boolean addQuestionnaire(@RequestBody Questionnaire questionnaire) {
        return questionnaireRepository.createQuestionnaire(questionnaire);
    }

}
