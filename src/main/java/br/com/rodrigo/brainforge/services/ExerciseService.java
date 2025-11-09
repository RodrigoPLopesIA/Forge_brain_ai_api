package br.com.rodrigo.brainforge.services;

import java.util.ArrayList;
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
import br.com.rodrigo.brainforge.dtos.ResponseExerciseIdDTO;
import br.com.rodrigo.brainforge.dtos.ResponseExerciseResultDTO;
import br.com.rodrigo.brainforge.dtos.ResponseQuestionResultDTO;
import br.com.rodrigo.brainforge.entities.AnsweredExercises;
import br.com.rodrigo.brainforge.entities.AnsweredQuestions;
import br.com.rodrigo.brainforge.entities.Exercise;
import br.com.rodrigo.brainforge.entities.Question;
import br.com.rodrigo.brainforge.mapper.ExerciseMapper;
import br.com.rodrigo.brainforge.mapper.QuestionMapper;
import br.com.rodrigo.brainforge.repositories.AnsweredExercisesRepository;
import br.com.rodrigo.brainforge.repositories.AnsweredQuestionsRepository;
import br.com.rodrigo.brainforge.repositories.ExerciseRepository;
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

    @Transactional
    public ResponseExerciseIdDTO answerExercise(UUID exerciseId, List<RequestExercisesAnswerDTO> answers) {
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

        double totalScore = 0.0;
        double userScore = 0.0;

        // 🔹 Primeiro cria o registro do AnsweredExercise
        AnsweredExercises answeredExercise = new AnsweredExercises();
        answeredExercise.setExercise(exercise);
        answeredExercise.setTotalScore(0.0);
        answeredExercise.setUserScore(0.0);

        answeredExercise = answeredExercisesRepository.save(answeredExercise);

        List<AnsweredQuestions> answeredQuestionsList = new ArrayList<>();

        for (Question question : exercise.getQuestions()) {
            String userAnswer = answerMap.get(question.getId());

            // Evita NullPointer caso a resposta não venha
            if (userAnswer == null) {
                throw new RuntimeException("Missing answer for question ID: " + question.getId());
            }

            boolean isCorrect = question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
            double score = isCorrect ? question.getScore() : 0.0;

            totalScore += question.getScore();
            userScore += score;

            AnsweredQuestions answered = AnsweredQuestions.builder()
                    .question(question)
                    .answeredExercise(answeredExercise)
                    .answer(userAnswer)
                    .isCorrect(isCorrect)
                    .scoreObtained(score)
                    .build();

            answeredQuestionsList.add(answered);
        }

        // 🔹 Associa as respostas à tentativa e salva tudo
        answeredExercise.setAnsweredQuestions(answeredQuestionsList);
        answeredExercise.setTotalScore(totalScore);
        answeredExercise.setUserScore(userScore);

        answeredExercisesRepository.save(answeredExercise);
        answeredQuestionsRepository.saveAll(answeredQuestionsList);

        return new ResponseExerciseIdDTO(exerciseId);
    }

    public List<ResponseExerciseResultDTO> getAllResponsesByExerciseId(UUID exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        // Busca todas as tentativas (respostas completas) do exercício
        List<AnsweredExercises> answeredExercises = answeredExercisesRepository.findByExerciseId(exerciseId);

        if (answeredExercises.isEmpty()) {
            throw new RuntimeException("Nenhuma resposta encontrada para este exercício");
        }

        List<ResponseExerciseResultDTO> results = answeredExercises.stream().map(answeredExercise -> {
            // Pega todas as perguntas respondidas associadas a esta tentativa
            List<AnsweredQuestions> answeredQuestions = answeredQuestionsRepository
                    .findByAnsweredExerciseId(answeredExercise.getId());

            double totalScore = exercise.getQuestions()
                    .stream()
                    .mapToDouble(Question::getScore)
                    .sum();

            double userScore = answeredQuestions.stream()
                    .mapToDouble(AnsweredQuestions::getScoreObtained)
                    .sum();

            // Monta a lista de perguntas com respostas e explicações
            List<ResponseQuestionResultDTO> questionResults = answeredQuestions.stream().map(a -> {
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
            }).collect(Collectors.toList());

            // Monta o resultado completo de uma tentativa
            return new ResponseExerciseResultDTO(
                    answeredExercise.getId(),
                    exercise.getId(),
                    exercise.getTheme(),
                    totalScore,
                    userScore,
                    questionResults,
                    answeredExercise.getCreatedAt());
        }).collect(Collectors.toList());

        return results;
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

}
