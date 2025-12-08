package br.com.rodrigo.brainforge.services.AIResources;

import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.services.AIResources.client.AIClient;
import br.com.rodrigo.brainforge.services.AIResources.factory.AIVerifyAnswerPromptFactory;

@Service
public class AIAnswerVerify {

    private final AIClient aiClient;
    private final AIVerifyAnswerPromptFactory promptFactory;

    public AIAnswerVerify(
            AIClient aiClient,
            AIVerifyAnswerPromptFactory promptFactory) {
        this.aiClient = aiClient;
        this.promptFactory = promptFactory;
    }

    public Boolean verifyAnswer(
            String question,
            String givenAnswer,
            String correctAnswer) {

        String prompt = promptFactory.create(question, givenAnswer, correctAnswer);

        String response = aiClient.ask(prompt);

        return Boolean.parseBoolean(response.trim());
    }

}
