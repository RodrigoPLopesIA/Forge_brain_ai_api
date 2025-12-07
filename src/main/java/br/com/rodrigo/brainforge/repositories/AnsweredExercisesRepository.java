package br.com.rodrigo.brainforge.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigo.brainforge.entities.AnsweredExercises;

public interface AnsweredExercisesRepository extends JpaRepository<AnsweredExercises, UUID> {

    List<AnsweredExercises> findByExerciseId(UUID exerciseId);

    Page<AnsweredExercises> findByExerciseId(UUID exerciseId, Pageable pageable);

}
