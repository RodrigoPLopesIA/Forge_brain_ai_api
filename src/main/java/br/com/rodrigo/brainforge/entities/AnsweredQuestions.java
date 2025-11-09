package br.com.rodrigo.brainforge.entities;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_responses")
public class AnsweredQuestions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private boolean isCorrect;

    @Column(nullable = false)
    private Long scoreObtained;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant answeredAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;
}
