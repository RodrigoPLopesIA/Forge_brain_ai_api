package br.com.rodrigo.brainforge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/questions")
public class QuestionController {
    


    @PostMapping
    public String create(@RequestBody String entity) {
        return entity;
    }
    
}
