package br.com.rodrigo.brainforge.dtos;

import java.time.Instant;
import java.util.UUID;

import br.com.rodrigo.brainforge.enums.DifficultyEnum;

public record ResponseExerciseDTO(
        UUID id,
        String theme,
        String description,
        DifficultyEnum difficulty,
        Instant createdAt,
        Instant updatedAt
) {}
