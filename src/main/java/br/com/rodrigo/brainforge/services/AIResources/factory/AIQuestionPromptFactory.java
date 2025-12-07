package br.com.rodrigo.brainforge.services.AIResources.factory;

import org.springframework.stereotype.Component;

@Component
public class AIQuestionPromptFactory {

    public String create(String theme, String type, String difficulty, long num, String desc) {

        return String.format("""
            THE QUESTIONS MUST BE IN PORTUGUESE.
            Generate %d %s questions about "%s"
            ...
            """, num, type, theme, difficulty, desc, type);
    }
}
