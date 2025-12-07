package br.com.rodrigo.brainforge.config;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.rodrigo.brainforge.dtos.error.ResponseErrorDTO;
import br.com.rodrigo.brainforge.exceptions.ExerciseMismatchException;
import br.com.rodrigo.brainforge.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerAdviceConfig {
    


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        ResponseErrorDTO errorDTO = new ResponseErrorDTO(
            ex.getMessage(),
            404,
            String.valueOf(System.currentTimeMillis()),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(404).body(errorDTO);
    }

    @ExceptionHandler(ExerciseMismatchException.class)
    public ResponseEntity<ResponseErrorDTO> handleExerciseMismatchException(ExerciseMismatchException ex, HttpServletRequest request) {
        ResponseErrorDTO errorDTO = new ResponseErrorDTO(
            ex.getMessage(),
            400,
            String.valueOf(System.currentTimeMillis()),
            request.getRequestURI(),
            null
        );
        return ResponseEntity.status(400).body(errorDTO);
    }
}
