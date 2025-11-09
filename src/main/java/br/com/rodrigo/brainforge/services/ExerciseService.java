package br.com.rodrigo.brainforge.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.RequestExercisesAnswerDTO;
import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.entities.AnsweredQuestions;
import br.com.rodrigo.brainforge.entities.Exercise;
import br.com.rodrigo.brainforge.entities.Question;
import br.com.rodrigo.brainforge.mapper.ExerciseMapper;
import br.com.rodrigo.brainforge.mapper.QuestionMapper;
import br.com.rodrigo.brainforge.repositories.AnsweredQuestionsRepository;
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

    @Autowired
    private AnsweredQuestionsRepository answeredQuestionsRepository;

    public ResponseExerciseDTO create(RequestExerciseDTO exercise) {

        Exercise newExercise = exerciseMapper.toEntity(exercise);

        List<ResponseAIQuestionDTO> aiQuestions = aiQuestionService.generateQuestions(
                exercise.theme(), exercise.type(), exercise.difficulty(), exercise.numberOfQuestions(),
                exercise.description());

        List<Question> questions = aiQuestions.stream()
                .map(questionMap -> {
                    Question question = questionMapper.toEntity(questionMap);
                    question.setExercise(newExercise);
                    return question;
                })
                .collect(Collectors.toList());

        newExercise.setQuestions(questions);

        Exercise saved = exerciseRepository.save(newExercise);

        return exerciseMapper.toResponseDTO(saved);
    }

    public List<ResponseExerciseDTO> index() {
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises.stream()
                .map(exercise -> exerciseMapper.toResponseDTO(exercise))
                .collect(Collectors.toList());
    }

    public ResponseExerciseDTO findById(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        return exerciseMapper.toResponseDTO(exercise);
    }

    public List<AnsweredQuestions> answerExercise(UUID exerciseId, List<RequestExercisesAnswerDTO> answers) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        if (exercise.getQuestions().isEmpty()) {
            throw new RuntimeException("Exercise has no questions");
        }

        if (answers.size() != exercise.getQuestions().size()) {
            throw new RuntimeException("Number of answers does not match number of questions");
        }

        Map<UUID, String> answerMap = answers.stream()
                .collect(Collectors.toMap(RequestExercisesAnswerDTO::questionId, RequestExercisesAnswerDTO::value));

        for (Question question : exercise.getQuestions()) {
            String userAnswer = answerMap.get(question.getId());

            boolean isCorrect = question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
            Double score = isCorrect ? question.getScore() : 0;

            AnsweredQuestions answered = new AnsweredQuestions();
            answered.setQuestion(question);
            answered.setAnswer(userAnswer);
            answered.setCorrect(isCorrect);
            answered.setScoreObtained(score);

            answeredQuestionsRepository.save(answered);

        }
        List<AnsweredQuestions> answeredList = answeredQuestionsRepository.findByQuestionExerciseId(exerciseId);
        return answeredList;
    }

}
