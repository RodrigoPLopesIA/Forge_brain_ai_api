package br.com.rodrigo.brainforge.dtos;

import java.util.List;

public record ResponseAIQuestionDTO(
    
        String statement,
        List<String> options,
        String correctAnswer,
        String type,
        String explanation,
        Double score) {

}
