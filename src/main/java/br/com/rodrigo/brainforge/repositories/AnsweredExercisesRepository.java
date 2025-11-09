package br.com.rodrigo.brainforge.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigo.brainforge.entities.AnsweredExercises;
import br.com.rodrigo.brainforge.entities.AnsweredQuestions;

public interface AnsweredExercisesRepository extends JpaRepository<AnsweredExercises, UUID> {

    List<AnsweredExercises> findByExerciseId(UUID exerciseId);
}
