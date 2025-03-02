package com.example.canvassing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Question {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String text;

    public Question(String question) {
        this.text = question;
    }
}
