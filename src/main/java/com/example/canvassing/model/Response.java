package com.example.canvassing.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Response {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long householdId;
    private Long questionId;
    private Long answerId;
    private String question;
    private String answer;
}
