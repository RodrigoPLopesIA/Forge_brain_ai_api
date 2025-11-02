package br.com.rodrigo.brainforge.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigo.brainforge.entities.Question;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    
}
