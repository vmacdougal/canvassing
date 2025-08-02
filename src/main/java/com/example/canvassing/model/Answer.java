package com.example.canvassing.model;

import lombok.*;

/**
 * a possible answer to a canvass question
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Answer {
    private Long id;

    private Long  questionId;
    private String text;

    public Answer(String answer) {
        this.text = answer;
    }
}
