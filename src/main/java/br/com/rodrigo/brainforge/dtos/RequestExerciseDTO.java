package br.com.rodrigo.brainforge.dtos;

public record RequestExerciseDTO(String theme, String description, String type, String difficulty, long numberOfQuestions) {
}
