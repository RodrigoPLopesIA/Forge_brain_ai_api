package br.com.rodrigo.brainforge.mapper;

import org.mapstruct.Mapper;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.entities.Exercise;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ResponseExerciseDTO toResponseDTO(Exercise exercise);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    Exercise toEntity(RequestExerciseDTO dto);
}
