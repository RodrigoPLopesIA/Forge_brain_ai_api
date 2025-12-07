package br.com.rodrigo.brainforge.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.dtos.RequestExerciseDTO;
import br.com.rodrigo.brainforge.dtos.RequestExercisesAnswerDTO;
import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseIdDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseResultDTO;
import br.com.rodrigo.brainforge.dtos.ResponseQuestionResultDTO;
import br.com.rodrigo.brainforge.entities.AnsweredExercises;
import br.com.rodrigo.brainforge.entities.AnsweredQuestions;
import br.com.rodrigo.brainforge.entities.Exercise;
import br.com.rodrigo.brainforge.entities.Question;
import br.com.rodrigo.brainforge.exceptions.ResourceNotFoundException;
import br.com.rodrigo.brainforge.mapper.ExerciseMapper;
import br.com.rodrigo.brainforge.mapper.QuestionMapper;
import br.com.rodrigo.brainforge.repositories.AnsweredExercisesRepository;
import br.com.rodrigo.brainforge.repositories.AnsweredQuestionsRepository;
import br.com.rodrigo.brainforge.repositories.ExerciseRepository;
import br.com.rodrigo.brainforge.services.AIResources.AIQuestionService;
import jakarta.transaction.Transactional;

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

        @Autowired
        private AnsweredExercisesRepository answeredExercisesRepository;

        @Autowired
        private AnsweredExercisesService answeredExercisesService;

        @Transactional
        public ResponseExerciseDTO create(RequestExerciseDTO request) {

                Exercise exercise = buildExercise(request);

                List<Question> questions = generateQuestionsForExercise(request, exercise);

                exercise.setQuestions(questions);

                Exercise savedExercise = exerciseRepository.save(exercise);

                return exerciseMapper.toResponseDTO(savedExercise);
        }

        public Page<ResponseExerciseDTO> index(Pageable pageable) {
                Page<Exercise> page = exerciseRepository.findAll(pageable);

                return page.map(exerciseMapper::toResponseDTO);
        }

        public ResponseExerciseDTO findById(UUID exerciseId) {
                Exercise exercise = exerciseRepository.findById(exerciseId)
                                .orElseThrow(() -> new RuntimeException("Exercise not found"));
                return exerciseMapper.toResponseDTO(exercise);
        }

        @Transactional
        public ResponseExerciseIdDTO answerExercise(UUID exerciseId, List<RequestExercisesAnswerDTO> answers) {
                Exercise exercise = loadExercise(exerciseId);
                validateExercise(exercise, answers);

                Map<UUID, String> answerMap = toAnswerMap(answers);

                AnsweredExercises answeredExercise = createInitialAnsweredExercise(exercise);

                List<AnsweredQuestions> answeredQuestions = evaluateAnswers(exercise, answeredExercise, answerMap);

                updateAnsweredExerciseScores(answeredExercise, answeredQuestions);

                persistAttempt(answeredExercise, answeredQuestions);

                return new ResponseExerciseIdDTO(exerciseId);
        }

        public Page<ResponseExerciseResultDTO> getAllResponsesByExerciseId(UUID exerciseId, Pageable pageable) {

                Exercise exercise = exerciseRepository.findById(exerciseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

                Page<AnsweredExercises> attempts = answeredExercisesRepository
                                .findByExerciseId(exerciseId, pageable);

                return attempts.map(attempt -> mapToExerciseResultDTO(exercise, attempt));
        }

        public ResponseExerciseResultDTO getAnsweredExercise(UUID exerciseId, UUID answeredExerciseId) {
                Exercise exercise = exerciseRepository.findById(exerciseId)
                                .orElseThrow(() -> new RuntimeException("Exercise not found"));

                // Busca a tentativa específica do exercício
                AnsweredExercises answeredExercise = answeredExercisesRepository
                                .findById(answeredExerciseId)
                                .orElseThrow(() -> new RuntimeException("Answered exercise not found"));

                if (!answeredExercise.getExercise().getId().equals(exerciseId)) {
                        throw new RuntimeException("This answered exercise does not belong to the specified exercise");
                }

                // Busca as perguntas respondidas dessa tentativa
                List<AnsweredQuestions> answeredQuestions = answeredQuestionsRepository
                                .findByAnsweredExerciseId(answeredExercise.getId());

                double totalScore = exercise.getQuestions()
                                .stream()
                                .mapToDouble(Question::getScore)
                                .sum();

                double userScore = answeredQuestions.stream()
                                .mapToDouble(AnsweredQuestions::getScoreObtained)
                                .sum();

                // Monta a lista de perguntas respondidas
                List<ResponseQuestionResultDTO> questionResults = answeredQuestions.stream()
                                .map(a -> {
                                        Question q = a.getQuestion();
                                        return new ResponseQuestionResultDTO(
                                                        q.getId(),
                                                        q.getTitle(),
                                                        q.getOptions(),
                                                        q.getCorrectAnswer(),
                                                        a.getAnswer(),
                                                        a.isCorrect(),
                                                        q.getScore(),
                                                        q.getExplanation());
                                })
                                .collect(Collectors.toList());

                // Retorna o resultado completo de uma tentativa
                return new ResponseExerciseResultDTO(
                                answeredExercise.getId(),
                                exercise.getId(),
                                exercise.getTheme(),
                                totalScore,
                                userScore,
                                questionResults,
                                answeredExercise.getCreatedAt());
        }

        @Transactional
        public void delete(UUID id) {
                Exercise exercise = exerciseRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));

                exerciseRepository.delete(exercise);
        }

        private void persistAttempt(
                        AnsweredExercises answeredExercise,
                        List<AnsweredQuestions> answeredQuestions) {
                answeredExercisesRepository.save(answeredExercise);
                answeredQuestionsRepository.saveAll(answeredQuestions);
        }

        private void updateAnsweredExerciseScores(
                        AnsweredExercises answeredExercise,
                        List<AnsweredQuestions> answeredQuestions) {
                double totalScore = answeredQuestions.stream()
                                .mapToDouble(a -> a.getQuestion().getScore())
                                .sum();

                double userScore = answeredQuestions.stream()
                                .mapToDouble(AnsweredQuestions::getScoreObtained)
                                .sum();

                answeredExercise.setAnsweredQuestions(answeredQuestions);
                answeredExercise.setTotalScore(totalScore);
                answeredExercise.setUserScore(userScore);
        }

        private boolean isCorrectAnswer(Question question, String userAnswer) {
                return question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
        }

        private List<AnsweredQuestions> evaluateAnswers(
                        Exercise exercise,
                        AnsweredExercises answeredExercise,
                        Map<UUID, String> answerMap) {
                List<AnsweredQuestions> list = new ArrayList<>();

                for (Question question : exercise.getQuestions()) {
                        String userAnswer = answerMap.get(question.getId());

                        if (userAnswer == null) {
                                throw new RuntimeException("Missing answer for question ID: " + question.getId());
                        }

                        boolean isCorrect = isCorrectAnswer(question, userAnswer);
                        double score = isCorrect ? question.getScore() : 0.0;

                        AnsweredQuestions answered = AnsweredQuestions.builder()
                                        .question(question)
                                        .answeredExercise(answeredExercise)
                                        .answer(userAnswer)
                                        .isCorrect(isCorrect)
                                        .scoreObtained(score)
                                        .build();

                        list.add(answered);
                }

                return list;
        }

        private AnsweredExercises createInitialAnsweredExercise(Exercise exercise) {
                AnsweredExercises answeredExercise = new AnsweredExercises();
                answeredExercise.setExercise(exercise);
                answeredExercise.setTotalScore(0.0);
                answeredExercise.setUserScore(0.0);
                return answeredExercisesRepository.save(answeredExercise);
        }

        private Exercise loadExercise(UUID exerciseId) {
                return exerciseRepository.findById(exerciseId)
                                .orElseThrow(() -> new RuntimeException("Exercise not found"));
        }

        private List<AnsweredExercises> loadExerciseAttempts(UUID exerciseId) {
                return answeredExercisesService.findByExerciseId(exerciseId);
        }

        private ResponseExerciseResultDTO mapToExerciseResultDTO(
                        Exercise exercise,
                        AnsweredExercises attempt) {
                List<AnsweredQuestions> answeredQuestions = loadAnsweredQuestions(attempt);

                double totalScore = exercise.getTotalScore();
                double userScore = calculateUserScore(answeredQuestions);

                List<ResponseQuestionResultDTO> questionResults = mapToQuestionResultDTOs(answeredQuestions);

                return buildResponseExerciseDTO(
                                exercise,
                                attempt,
                                totalScore,
                                userScore,
                                questionResults);
        }

        private void validateExercise(Exercise exercise, List<RequestExercisesAnswerDTO> answers) {
                if (exercise.getQuestions().isEmpty()) {
                        throw new RuntimeException("Exercise has no questions");
                }

                if (answers.size() != exercise.getQuestions().size()) {
                        throw new RuntimeException("Number of answers does not match number of questions");
                }
        }

        private Map<UUID, String> toAnswerMap(List<RequestExercisesAnswerDTO> answers) {
                return answers.stream()
                                .collect(Collectors.toMap(RequestExercisesAnswerDTO::questionId,
                                                RequestExercisesAnswerDTO::value));
        }

        private List<AnsweredQuestions> loadAnsweredQuestions(AnsweredExercises attempt) {
                return answeredQuestionsRepository.findByAnsweredExerciseId(attempt.getId());
        }

        private double calculateUserScore(List<AnsweredQuestions> answeredQuestions) {
                return answeredQuestions.stream()
                                .mapToDouble(AnsweredQuestions::getScoreObtained)
                                .sum();
        }

        private List<ResponseQuestionResultDTO> mapToQuestionResultDTOs(
                        List<AnsweredQuestions> answeredQuestions) {
                return answeredQuestions.stream()
                                .map(this::mapToQuestionResultDTO)
                                .collect(Collectors.toList());
        }

        private ResponseQuestionResultDTO mapToQuestionResultDTO(AnsweredQuestions a) {
                Question q = a.getQuestion();

                return new ResponseQuestionResultDTO(
                                q.getId(),
                                q.getTitle(),
                                q.getOptions(),
                                q.getCorrectAnswer(),
                                a.getAnswer(),
                                a.isCorrect(),
                                q.getScore(),
                                q.getExplanation());
        }

        private ResponseExerciseResultDTO buildResponseExerciseDTO(
                        Exercise exercise,
                        AnsweredExercises attempt,
                        double totalScore,
                        double userScore,
                        List<ResponseQuestionResultDTO> questionResults) {
                return new ResponseExerciseResultDTO(
                                attempt.getId(),
                                exercise.getId(),
                                exercise.getTheme(),
                                totalScore,
                                userScore,
                                questionResults,
                                attempt.getCreatedAt());
        }

        private Exercise buildExercise(RequestExerciseDTO request) {
                return exerciseMapper.toEntity(request);
        }

        private List<Question> generateQuestionsForExercise(RequestExerciseDTO request, Exercise exercise) {

                List<ResponseAIQuestionDTO> aiQuestions = aiQuestionService.generateQuestions(
                                request.theme(),
                                request.type(),
                                request.difficulty(),
                                request.numberOfQuestions(),
                                request.description());

                return aiQuestions.stream()
                                .map(questionDto -> mapQuestionToExercise(questionDto, exercise))
                                .collect(Collectors.toList());
        }

        private Question mapQuestionToExercise(ResponseAIQuestionDTO questionDto, Exercise exercise) {
                Question question = questionMapper.toEntity(questionDto);
                question.setExercise(exercise);
                return question;
        }

}
