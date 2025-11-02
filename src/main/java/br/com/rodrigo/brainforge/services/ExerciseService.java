package br.com.rodrigo.brainforge.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.entities.Exercise;
import br.com.rodrigo.brainforge.enums.DifficultyEnum;
import br.com.rodrigo.brainforge.mapper.ExerciseMapper;
import br.com.rodrigo.brainforge.repositories.ExerciseRepository;

@Service
public class ExerciseService {


    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseMapper exerciseMapper;


    public ResponseExerciseDTO create(RequestExerciseDTO exercise) {

        Exercise newExercise = exerciseMapper.toEntity(exercise);
        
        exerciseRepository.save(newExercise);

        return exerciseMapper.toResponseDTO(newExercise);
    }

    public List<ResponseExerciseDTO> index() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                        .map(exercise -> exerciseMapper.toResponseDTO(exercise))
                        .collect(Collectors.toList());
    } 
}
