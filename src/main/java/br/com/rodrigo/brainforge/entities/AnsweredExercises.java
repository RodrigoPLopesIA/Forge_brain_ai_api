package br.com.rodrigo.brainforge.entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answered_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AnsweredExercises {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // 🔗 Ligação com o exercício original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    // 🔢 Pontuação obtida pelo usuário
    @Column(nullable = false)
    private Double userScore;

    // 🔢 Pontuação máxima (soma de todas as questões)
    @Column(nullable = false)
    private Double totalScore;

    // 🔗 Todas as perguntas respondidas dessa tentativa
    @OneToMany(mappedBy = "answeredExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnsweredQuestions> answeredQuestions;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;
}
