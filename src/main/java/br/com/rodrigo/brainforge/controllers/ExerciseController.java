package br.com.rodrigo.brainforge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/exercises")
@RestController
public class ExerciseController {
    

    @PostMapping
    public ResponseEntity<String> create(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
    }
    
}
