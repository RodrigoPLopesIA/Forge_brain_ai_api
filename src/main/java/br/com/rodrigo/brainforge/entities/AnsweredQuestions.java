package br.com.rodrigo.brainforge.entities;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answered_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AnsweredQuestions {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 🔗 Pergunta original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 🔗 Tentativa de exercício a que essa resposta pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answered_exercise_id", nullable = false)
    private AnsweredExercises answeredExercise;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(nullable = false)
    private boolean isCorrect;

    @Column(nullable = false)
    private Double scoreObtained;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant answeredAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;


 
}
