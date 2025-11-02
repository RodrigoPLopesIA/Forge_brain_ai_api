package br.com.rodrigo.brainforge.services;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AIQuestionService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public AIQuestionService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public List<Map<String, Object>> generateQuestions(String theme, String type, String difficulty) {

        String prompt = String.format("""
                Generate 10 %s questions about the theme "%s"
                with difficulty level "%s".
                Return ONLY JSON in this structure:
                [
                  {
                    "statement": "Question text here",
                    "options": ["A", "B", "C", "D"],
                    "correctAnswer": "A",
                    "type": "%s"
                  }
                ]
                """, type, theme, difficulty, type);

        // ✅ Nova forma simplificada
        String response = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        try {
            return mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing AI response", e);
        }
    }
}
