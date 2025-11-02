package br.com.rodrigo.brainforge.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.entities.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    
    @Mapping(target = "title", source = "statement")
    @Mapping(target = "options", expression = "java(aiResponse.getOptions() == null ? null : String.join(\"\\n\", aiResponse.getOptions()))")
    @Mapping(target = "correctAnswer", source = "correctAnswer")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Question toEntity(ResponseAIQuestionDTO aiResponse);

    
}
