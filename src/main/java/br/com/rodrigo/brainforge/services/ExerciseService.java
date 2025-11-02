package br.com.rodrigo.brainforge.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.entities.Exercise;
import br.com.rodrigo.brainforge.entities.Question;
import br.com.rodrigo.brainforge.mapper.ExerciseMapper;
import br.com.rodrigo.brainforge.mapper.QuestionMapper;
import br.com.rodrigo.brainforge.repositories.ExerciseRepository;

@Service
public class ExerciseService {


    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseMapper exerciseMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AIQuestionService aiQuestionService;

        public ResponseExerciseDTO create(RequestExerciseDTO exercise) {

            Exercise newExercise = exerciseMapper.toEntity(exercise);

            List<ResponseAIQuestionDTO> aiQuestions = aiQuestionService.generateQuestionsMock(
                    exercise.theme(), exercise.type(), exercise.difficulty());

            // Mapper: converte a resposta da IA em entidades Question e liga cada Question ao Exercise
            List<Question> questions = aiQuestions.stream()
                    .map(questionMapper::toEntity)
                    .collect(Collectors.toList());
            
            newExercise.setQuestions(questions);

            // Persistir exercício (e, assumindo cascade apropriado em Exercise.questions, também persistirá as questions)
            Exercise saved = exerciseRepository.save(newExercise);

            return exerciseMapper.toResponseDTO(saved);
        }


    public List<ResponseExerciseDTO> index() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                        .map(exercise -> exerciseMapper.toResponseDTO(exercise))
                        .collect(Collectors.toList());
    } 
}
