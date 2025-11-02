package br.com.rodrigo.brainforge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.services.ExerciseService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/exercises")
@RestController
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseService;
    
    @PostMapping
    public ResponseEntity<ResponseExerciseDTO> create(@RequestBody RequestExerciseDTO exercise) {
        ResponseExerciseDTO response = exerciseService.create(exercise);
        return ResponseEntity.ok(response);
    }
    
}
