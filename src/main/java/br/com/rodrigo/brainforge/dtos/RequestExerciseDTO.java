package br.com.rodrigo.brainforge.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RequestExerciseDTO(@NotBlank String theme, @NotBlank String description, @NotBlank String type, @NotBlank String difficulty, @NotNull Long numberOfQuestions) {
}
