package br.com.rodrigo.brainforge.dtos;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.rodrigo.brainforge.entities.Question;
import br.com.rodrigo.brainforge.enums.DifficultyEnum;

public record ResponseExerciseDTO(
        UUID id,
        String theme,
        String description,
        DifficultyEnum difficulty,
        List<Question> questions,
        Instant createdAt,
        Instant updatedAt
) {}
