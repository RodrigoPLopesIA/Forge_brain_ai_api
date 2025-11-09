package br.com.rodrigo.brainforge.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.enums.QuestionType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AIQuestionService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AIQuestionService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<ResponseAIQuestionDTO> generateQuestions(String theme, String type, String difficulty,
            long numberOfQuestions, String description) {

        String prompt = String.format("""
                THE QUESTIONS MUST BE IN PORTUGUESE.
                Generate %d %s questions about the theme "%s"
                with difficulty level "%s".
                The questions must be related to the following description: "%s".
                The value type must be EQUAL to one of these options: MULTIPLE_CHOICE or DISCURSIVE.
                If value type is ANY, mix 50%% MULTIPLE_CHOICE and 50%% DISCURSIVE questions.

                Each question must have a "score" field so that the total sum of all scores equals 10.
                For example:
                  - If there are 10 questions, each question must have score = 1.
                  - If there are 5 questions, each question must have score = 2.
                  - If there are 4 questions, each question must have score = 2.5.

                For MULTIPLE_CHOICE questions:
                  - "options" must be an array of possible answers (strings).
                  - "correctAnswer" must be exactly one of the values present in "options".

                For DISCURSIVE questions:
                  - Do not include the "options" field.
                  - "correctAnswer" must contain a short, clear, and correct written answer.

                Return ONLY JSON in this structure:
                [
                  {
                    "statement": "Question text here",
                    "options": ["Option 1", "Option 2", "Option 3"],
                    "correctAnswer": "Option 2",
                    "type": "%s",
                    "score": 1
                  }
                ]
                """, numberOfQuestions, type, theme, difficulty, description, type);

        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        // Limpar possíveis marcadores extras
        response = response.trim();
        if (response.startsWith("json")) {
            response = response.substring(4).trim();
        }
        if (response.startsWith("```json")) {
            response = response.substring(7).trim();
        }
        if (response.endsWith("```")) {
            response = response.substring(0, response.length() - 3).trim(); // remove ```
        }

        try {
            return mapper.readValue(response, new TypeReference<List<ResponseAIQuestionDTO>>() {
            });
        } catch (IOException e) {
            log.error("Failed parsing AI JSON: {}", response);
            throw new RuntimeException("Error parsing AI response", e);
        }

    }

    public List<ResponseAIQuestionDTO> generateQuestionsMock(String theme, String type, String difficulty) {
        String json = """
                [
                    {
                        "statement": "What is 2+2?",
                        "options": ["2","3","4","5"],
                        "correctAnswer": "4",
                        "type": "MULTIPLE_CHOICE"
                    },
                    {
                        "statement": "Explain gravity.",
                        "options": [],
                        "correctAnswer": "Gravity pulls objects.",
                        "type": "DISCURSIVE"
                    }
                ]
                """;

        try {
            return new ObjectMapper().readValue(json, new TypeReference<List<ResponseAIQuestionDTO>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
