package br.com.rodrigo.brainforge.services.AIResources.parser;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.exceptions.AIParsingException;

@Component 
public class AIQuestionParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public List<ResponseAIQuestionDTO> parseQuestions(String json) {

        try {
            List<ResponseAIQuestionDTO> list =
                mapper.readValue(json, new TypeReference<List<ResponseAIQuestionDTO>>(){});
            
            validate(list);
            return list;

        } catch (Exception e) {
            throw new AIParsingException("Invalid AI JSON", e);
        }
    }

    private void validate(List<ResponseAIQuestionDTO> list) {
        if (list == null || list.isEmpty())
            throw new AIParsingException("AI returned empty list");

    }
}

