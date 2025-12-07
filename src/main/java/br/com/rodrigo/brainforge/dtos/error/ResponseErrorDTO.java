package br.com.rodrigo.brainforge.dtos.error;

import java.util.Map;

public record ResponseErrorDTO(String message, int status, String timestamp, String path, Map<String, String> errors) {
    
}
