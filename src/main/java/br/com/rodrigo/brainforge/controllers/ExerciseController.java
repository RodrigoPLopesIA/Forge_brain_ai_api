package br.com.rodrigo.brainforge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.enums.QuestionType;
import br.com.rodrigo.brainforge.services.AIQuestionService;
import br.com.rodrigo.brainforge.services.ExerciseService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RequestMapping("/exercises")
@RestController
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private AIQuestionService aiQuestionService;
    
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody RequestExerciseDTO exercise) {
        // ResponseExerciseDTO response = exerciseService.create(exercise);
        Object questions = aiQuestionService.generateQuestionsMock(exercise.theme(), QuestionType.MULTIPLE_CHOICE.name(), exercise.difficulty());;
        return ResponseEntity.ok(questions);
    }

    @GetMapping
    public ResponseEntity<List<ResponseExerciseDTO>> index() {
        List<ResponseExerciseDTO> response = exerciseService.index();
        return ResponseEntity.ok(response);
    }
    
    
}
