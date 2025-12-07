package br.com.rodrigo.brainforge.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.entities.AnsweredExercises;
import br.com.rodrigo.brainforge.repositories.AnsweredExercisesRepository;

@Service
public class AnsweredExercisesService {
    
    @Autowired
    private AnsweredExercisesRepository answeredExercisesRepository;


    public List<AnsweredExercises> findByExerciseId(UUID exerciseId) {
        List<AnsweredExercises> answeredExercises = answeredExercisesRepository.findByExerciseId(exerciseId);

        if (answeredExercises.isEmpty()) {
            throw new RuntimeException("Nenhuma resposta encontrada para este exercício");
        }
        return answeredExercises;
    }
}
