package com.example.canvassing.model;

import lombok.*;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Response {
    private Long id;

    private Long householdId;
    private Long questionId;
    private Long answerId;
    private String question;
    private String answer;
}
