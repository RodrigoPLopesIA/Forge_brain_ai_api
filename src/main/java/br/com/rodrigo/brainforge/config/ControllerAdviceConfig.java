package br.com.rodrigo.brainforge.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.rodrigo.brainforge.dtos.error.ResponseErrorDTO;
import br.com.rodrigo.brainforge.exceptions.ExerciseMismatchException;
import br.com.rodrigo.brainforge.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ControllerAdviceConfig {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex,
            HttpServletRequest request) {
        ResponseErrorDTO errorDTO = new ResponseErrorDTO(
                ex.getMessage(),
                404,
                String.valueOf(System.currentTimeMillis()),
                request.getRequestURI(),
                null);
        return ResponseEntity.status(404).body(errorDTO);
    }

    @ExceptionHandler(ExerciseMismatchException.class)
    public ResponseEntity<ResponseErrorDTO> handleExerciseMismatchException(ExerciseMismatchException ex,
            HttpServletRequest request) {
        ResponseErrorDTO errorDTO = new ResponseErrorDTO(
                ex.getMessage(),
                400,
                String.valueOf(System.currentTimeMillis()),
                request.getRequestURI(),
                null);
        return ResponseEntity.status(400).body(errorDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<String, String>();

        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()) 
            );
        ResponseErrorDTO errorDTO = new ResponseErrorDTO(
            "Validation failed",
            400,
            String.valueOf(System.currentTimeMillis()),
            request.getRequestURI(),
            errors
        );
        return ResponseEntity.status(400).body(errorDTO);
    }
}