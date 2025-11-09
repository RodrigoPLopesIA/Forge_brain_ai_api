package br.com.rodrigo.brainforge.dtos;

import java.util.List;
import java.util.UUID;

public record ResponseExerciseResultDTO(
    UUID exerciseId,
    String title,
    double totalScore,
    double userScore,
    List<ResponseQuestionResultDTO> questions
) {

    // getters e setters
}

