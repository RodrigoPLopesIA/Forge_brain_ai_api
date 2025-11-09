package br.com.rodrigo.brainforge.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestExercisesAnswerDTO(@NotNull UUID questionId, @NotBlank String value) {
    
}
