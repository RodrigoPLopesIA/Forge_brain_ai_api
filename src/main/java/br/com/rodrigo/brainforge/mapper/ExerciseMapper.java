package br.com.rodrigo.brainforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.entities.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ResponseExerciseDTO toResponseDTO(Exercise exercise);

    @Mapping(target = "difficulty", source = "dto.difficulty")
    @Mapping(target = "theme", source = "dto.theme")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "questions", ignore = true)
    Exercise toEntity(RequestExerciseDTO dto);
}
