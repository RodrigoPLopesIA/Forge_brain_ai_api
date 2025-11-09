package br.com.rodrigo.brainforge.dtos;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ResponseExerciseResultDTO(
    UUID id,
    UUID exerciseId,
    String title,
    double totalScore,
    double userScore,
    List<ResponseQuestionResultDTO> questions,
    Instant createdAt
) {
}

