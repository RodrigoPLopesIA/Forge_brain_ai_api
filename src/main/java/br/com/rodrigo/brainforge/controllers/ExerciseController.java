package br.com.rodrigo.brainforge.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.RequestExercisesAnswerDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseIdDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseResultDTO;
import br.com.rodrigo.brainforge.services.ExerciseService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RequestMapping("/exercises")
@RestController
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ResponseExerciseDTO> create(@RequestBody @Valid RequestExerciseDTO exercise) {
        ResponseExerciseDTO response = exerciseService.create(exercise);
        var uri = UriComponentsBuilder.fromPath("/exercises/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ResponseExerciseDTO>> index(Pageable pageable) {
        Page<ResponseExerciseDTO> response = exerciseService.index(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseExerciseDTO> show(@PathVariable UUID id) {
        ResponseExerciseDTO response = exerciseService.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<ResponseExerciseIdDTO> seed(
            @PathVariable("id") UUID exerciseId,
            @RequestBody @Valid List<RequestExercisesAnswerDTO> answers) {

        ResponseExerciseIdDTO exerciseIdDTO = exerciseService.answerExercise(exerciseId, answers);
        return ResponseEntity.ok().body(exerciseIdDTO);
    }

    // 🔥 (OPCIONAL MAS RECOMENDADO) — Paginação nas respostas
    @GetMapping("/{exerciseId}/answers")
    public ResponseEntity<Page<ResponseExerciseResultDTO>> getAllResponsesByExerciseId(
            @PathVariable UUID exerciseId,
            Pageable pageable) {

        Page<ResponseExerciseResultDTO> result = exerciseService.getAllResponsesByExerciseId(exerciseId, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{exerciseId}/answered/{answeredExerciseId}")
    public ResponseEntity<ResponseExerciseResultDTO> getAnsweredExercise(
            @PathVariable UUID exerciseId,
            @PathVariable UUID answeredExerciseId) {

        ResponseExerciseResultDTO result = exerciseService.getAnsweredExercise(exerciseId, answeredExerciseId);
        return ResponseEntity.ok(result);
    }
}
