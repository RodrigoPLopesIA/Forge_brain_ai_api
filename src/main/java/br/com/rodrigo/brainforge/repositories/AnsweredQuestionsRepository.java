package br.com.rodrigo.brainforge.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.rodrigo.brainforge.entities.AnsweredQuestions;

public interface AnsweredQuestionsRepository extends JpaRepository<AnsweredQuestions, UUID> {
    
}
