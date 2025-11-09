package br.com.rodrigo.brainforge.dtos;

import java.util.List;
import java.util.UUID;

public record ResponseQuestionResultDTO(
        UUID id,
        String statement,
        List<String> options,
        String correctAnswer,
        String userAnswer,
        boolean isCorrect,
        double score,
        String explanation) {

}
