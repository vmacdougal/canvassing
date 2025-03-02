package com.example.demo.model;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Questionnaire {
    //the order matters; often certain questions only make sense after certain others
    private Map<Question, List<Answer>> items = new LinkedHashMap<>();

    public void addItem(String question, List<String> answers) {
        Question questionObj = new  Question(question);
        List<Answer> answerList = answers.stream()
                .map(Answer::new)
                .toList();
        items.put(questionObj, answerList);
    }

}
