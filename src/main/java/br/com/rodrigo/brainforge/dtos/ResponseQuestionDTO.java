package br.com.rodrigo.brainforge.dtos;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.rodrigo.brainforge.enums.QuestionType;

public record ResponseQuestionDTO(UUID id, String title, List<String> options, QuestionType type, Double score, Instant createdAt, Instant updatedAt) {
    
}
