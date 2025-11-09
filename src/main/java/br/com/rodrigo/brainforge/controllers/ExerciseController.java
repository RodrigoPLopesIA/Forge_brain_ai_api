package br.com.rodrigo.brainforge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.RequestExercisesAnswerDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.services.ExerciseService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RequestMapping("/exercises")
@RestController
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseService;


    @PostMapping
    public ResponseEntity<ResponseExerciseDTO> create(@RequestBody RequestExerciseDTO exercise) {
        ResponseExerciseDTO response = exerciseService.create(exercise);
        var uri = UriComponentsBuilder.fromPath("/exercises/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResponseExerciseDTO>> index() {
        List<ResponseExerciseDTO> response = exerciseService.index();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseExerciseDTO> show(@PathVariable UUID id) {
        ResponseExerciseDTO response = exerciseService.findById(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/answer")
    public ResponseEntity<Void> seed(@PathVariable("id") UUID exerciseId, @RequestBody List<RequestExercisesAnswerDTO> answers) {
        System.out.println("Received answers for exercise " + exerciseId + ": " + answers);
        exerciseService.answerExercise(exerciseId, answers);
        return ResponseEntity.ok().build();
    }
}
