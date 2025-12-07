package br.com.rodrigo.brainforge.config;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.rodrigo.brainforge.dtos.error.ResponseErrorDTO;
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
}
