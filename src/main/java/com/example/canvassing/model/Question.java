package com.example.canvassing.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Question {
    Long id;

    private String text;

    public Question(String question) {
        this.text = question;
    }
}
